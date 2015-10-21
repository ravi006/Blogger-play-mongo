package models

import play.api.libs.json.Json
import reactivemongo.bson.BSONObjectID

/**
 * Created by ravi on 16/10/15.
 */
case class User(_id: BSONObjectID, email: String, password: String, firstName: String, lastName: String)


object User {

  import play.modules.reactivemongo.json.BSONFormats._

  implicit val userFormat = Json.format[User]
}

case class UserLogin(email: String, password: String)


object UserLogin {

  import play.modules.reactivemongo.json.BSONFormats._

  implicit val userFormat = Json.format[UserLogin]
}
