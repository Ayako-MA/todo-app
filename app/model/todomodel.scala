package model

// ToDO
case class ToDo(
               Id      : Option[Long],
               Title   : String,
               Content : String,
               Status  : String,
               Category: String
               )