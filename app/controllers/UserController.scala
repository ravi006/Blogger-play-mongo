package controllers

import java.util.concurrent.TimeoutException
import javax.inject.Inject

import akka.actor.Status.{Failure, Success}
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id
import models.{UserLogin, User}
import org.mindrot.jbcrypt.BCrypt
import org.mindrot.jbcrypt.BCrypt
import play.api.Logger
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.MessagesApi
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import play.modules.reactivemongo.json.collection.JSONCollection
import play.modules.reactivemongo.{ReactiveMongoComponents, MongoController, ReactiveMongoApi}
import reactivemongo.bson.{BSONString, BSONDocument, BSONObjectID}
import views.html

import scala.concurrent.Future

/**
 * Created by ravi on 9/10/15.
 */
class UserController @Inject()(val reactiveMongoApi: ReactiveMongoApi, val messagesApi: MessagesApi) extends Controller with MongoController with ReactiveMongoComponents {

  def collection: JSONCollection = db.collection[JSONCollection]("user")


  /**
   * Used to create User
   */
  val userForm = Form(mapping(
    "id" -> ignored(BSONObjectID.generate: BSONObjectID),
    "email" -> nonEmptyText,
    "password" -> nonEmptyText,
    "firstName" -> nonEmptyText,
    "lastName" -> nonEmptyText)(User.apply)(User.unapply))

  /*  case class UserMessageForm (email: String, password: String, firstName: String, lastName: String){
      import models._
      def user: User = User(BSONObjectID.generate, email, password, firstName, lastName)
    }
    import play.modules.reactivemongo.json.BSONFormats._
    implicit val userUserFormFormat = Json.format[UserMessageForm]*/


  /**
   * Methode for Creating User
   * @return
   */
  def createUser = Action { request =>
    implicit val msg = messagesApi.preferred(request)
    Ok(html.createUserForm(userForm))
  }

  import scala.concurrent.ExecutionContext.Implicits.global

  def userSave = Action { implicit request =>
    userForm.bindFromRequest.fold(
    { formWithErrors =>
      implicit val msg = messagesApi.preferred(request)
      Future.successful(BadRequest(html.createUserForm(formWithErrors)))
    },
    user => {
      val futureUpdateUser = collection.insert(user.copy(_id = BSONObjectID.generate, user.email, passwordHash(user.password), user.firstName, user.lastName))

    /*  futureUpdateUser.onComplete {
        case Success() => {

        }
        case Failure() => {

        }
      }*/

    }
    )
    Redirect(routes.UserController.blog())
  }


/*  def userCreate = Action { implicit request =>
    userForm.bindFromRequest.fold(
    { formWithErrors =>
      implicit val msg = messagesApi.preferred(request)
      Future.successful(BadRequest(html.createUserForm(formWithErrors)))
    },
    user => {
      val modifier = BSONDocument(
        "$SET" -> BSONDocument(
          "_id" -> BSONObjectID.generate,
          "email" -> BSONString(user.email),
          "password" -> BSONString(passwordHash(user.password)),
          "firstName" -> BSONString(user.firstName),
          "lastName" -> BSONString(user.lastName)
        )
      )
      val futureUpdateUser: Future[WriteResult] = collection.insert(modifier)
    }
    )
    //Redirect(routes.Application.index)
    Ok("Hello Play")
  }*/

  def passwordHash(password: String): String = {
    BCrypt.hashpw(password, BCrypt.gensalt(12))
  }

  def checkPassword(password: String, hashPassword: String): Boolean = {
    BCrypt.checkpw(password, hashPassword)
  }

  def userPasswordUpdate(id: BSONObjectID) = Action { implicit request =>
    userForm.bindFromRequest.fold(
    { formWithErrors =>
      implicit val msg = messagesApi.preferred(request)
      Future.successful(BadRequest(html.createUserForm(formWithErrors)))
    },
    user => {
      //val objectId = new BSONObjectID(id)
      val modifier = BSONDocument(
        "$SET" -> BSONDocument(
          "password" -> BSONString(passwordHash(user.password))
        )
      )
      //val futureUpdateUser = collection.update(BSONDocument("_id" -> id), modifier)
    }
    )
    //Redirect(routes.Application.index)
    Ok("Hello Play")
  }

  def blog = Action {
    blogPage
  }

  val blogPage = Ok(views.html.blogging("Hello"))


  def findByEmail(query: BSONDocument) =  {
    val cursor = collection.find(query).cursor[User]().collect[List]()
  }

  val userLoginForm = Form(mapping(
    "email" -> nonEmptyText,
    "password" -> nonEmptyText)(UserLogin.apply)(UserLogin.unapply))

  def userLogin(email: String) = Action { implicit request =>
    userLoginForm.bindFromRequest.fold(
    { formWithErrors =>
      implicit val msg = messagesApi.preferred(request)
      Future.successful(BadRequest(html.loginForm(formWithErrors)))
    },
    user => {
      val futureUserLogin = Future(findByEmail(BSONDocument("email" -> BSONString(email))))
      futureUserLogin.onComplete{
        case Success() =>{
          Redirect(routes.UserController.blog())
        }
        case Failure() => {
          Redirect(routes.UserController.blog())
        }
      }
    }
    )
    Redirect(routes.UserController.blog())
  }
}
