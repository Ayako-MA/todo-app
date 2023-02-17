package controllers.todo

import javax.inject.{Inject, Singleton}
import play.api.mvc.ControllerComponents
import play.api.mvc.BaseController
import play.api.mvc.Request
import play.api.mvc.AnyContent
import model.ViewValueHome
import play.api.i18n.I18nSupport
import play.api.data._
import play.api.data.Forms._

import model.ToDo

/**
 * @SingletonでPlayFrameworkの管理下でSingletonオブジェクトとして本クラスを扱う指定をする
 * @Injectでconstructorの引数をDIする
 *   BaseControllerにはprotected の controllerComponentsが存在するため、そこに代入される。
 */

case class TodoFormData(title:String, content: String, category: String )
@Singleton
class TodoController @Inject()(val controllerComponents: ControllerComponents) extends BaseController with I18nSupport {
  val header = ViewValueHome(
    title = "Todo一覧",
    cssSrc = Seq("main.css"),
    jsSrc = Seq("main.js")
  )

  val sampleTodo = ToDo(
    Id       = Some(1),
    Title    = "today",
    Content  = "create to do App",
    Status   = "進行中",
    Category = "勉強"
  )

  val sampleCategory: Map[String, String] = Map(("研究" -> "#FFCCFF"),("勉強" -> "#FFFFCC"),("生活" -> "#99CCFF"),("その他" -> "#99FFCC"))

  val todos = scala.collection.mutable.ArrayBuffer(sampleTodo)

  val form = Form(
    // html formのnameがcontentのものを140文字以下の必須文字列に設定する
    mapping(
      "title"   -> nonEmptyText(maxLength = 140),
      "content" -> nonEmptyText(maxLength = 140),
      "category"-> nonEmptyText(maxLength = 140),

    )(TodoFormData.apply)(TodoFormData.unapply),

  )

  def list() = Action { implicit request: Request[AnyContent] =>
    // Ok()はステータスコードが200な、Resultをreturnします
    Ok(views.html.todo.list(todos.toSeq, sampleCategory))
  }


  def register() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.todo.store(form))
  }

  // コンパイルエラー回避用に何もしない登録用のstoreメソッドも作成
  def store() = Action { implicit request: Request[AnyContent] =>
    // foldでデータ受け取りの成功、失敗を分岐しつつ処理が行える
    form.bindFromRequest().fold(
      // 処理が失敗した場合に呼び出される関数
      // 処理失敗の例: バリデーションエラー
      (formWithErrors: Form[TodoFormData]) => {
        BadRequest(views.html.todo.store(formWithErrors))
      },

      // 処理が成功した場合に呼び出される関数
      (todoFormData: TodoFormData) => {
        // 登録処理としてSeqに画面から受け取ったコンテンツを持つTweetを追加
        todos += ToDo(Some(todos.size + 1L), todoFormData.title, todoFormData.content, Status = "Todo", todoFormData.category)
        // 登録が完了したら一覧画面へリダイレクトする
        Redirect("/todo/list")
        // 以下のような書き方も可能です。基本的にはtwirl側と同じです
        // 自分自身がcontrollers.tweetパッケージに属しているのでcontrollers.tweetの部分が省略されています。
        // Redirect(routes.TweetController.list())
      }
    )
  }


  //編集画面開く
  def edit(id: Long) = Action { implicit request: Request[AnyContent] =>
    todos.find(_.Id.exists(_ == id)) match {
      case Some(todo) =>
        Ok(views.html.todo.edit(
          id, // データを識別するためのidを渡す
          form.fill(TodoFormData(todo.Title, todo.Content, todo.Category)) // fillでformに値を詰める
        ))
      case None =>
        NotFound(views.html.error.page404())
    }
  }

  //更新
  def update(id: Long) = Action { implicit request: Request[AnyContent] =>
    form.bindFromRequest().fold(
      (formWithErrors: Form[TodoFormData]) => {
        BadRequest(views.html.todo.edit(id, formWithErrors))
      },
      (data: TodoFormData) => {
        todos.find(_.Id.exists(_ == id)) match {
          case Some(todo) =>
            // indexは0からのため-1
            todos.update(id.toInt - 1, ToDo(Some(id), data.title, data.content,Status ="進行中", data.category))
            Redirect(routes.TodoController.list())
          case None =>
            NotFound(views.html.error.page404())
        }
      }
    )
  }

  //削除
  def delete() = Action{implicit request: Request[AnyContent] =>
    //requestから直接値を取得する
    val idOpt = request.body.asFormUrlEncoded.get("id").headOption
    //id,値があるときに削除
    todos.find(_.Id.map(_.toString) == idOpt) match {
      case Some(todo) =>
        todos -= todo
        //リダイレクト
        Redirect(routes.TodoController.list())
      case None       =>
        NotFound(views.html.error.page404())
    }
  }

}