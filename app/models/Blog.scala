package models

import java.util.Date

/**
 * Created by ravi on 8/10/15.
 */
case class BlogMessages(userName: String, message: String, _date: Date)

object BlogMessages {

  import play.api.libs.json.Json
  import play.modules.reactivemongo.json.BSONFormats._

  implicit val blogMessagesFormat = Json.format[BlogMessages]
}