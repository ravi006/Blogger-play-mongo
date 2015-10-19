package service

import models.User
import play.api.libs.json.JsValue

import scala.concurrent.Future
import scala.util.Try

/**
 * Created by ravi on 19/10/15.
 */
trait UserService {

  def create(c: User): Future[Try[User]]

  def read(id: String): Future[Option[User]]

  def update(id: Long, updates: JsValue): Future[Try[Unit]]

  def delete(id: String): Future[Try[Unit]]

  def findAll(): Future[List[User]]

}
