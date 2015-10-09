package models

import java.util.Date

import reactivemongo.bson.BSONObjectID

/**
 * Created by ravi on 8/10/15.
 */
case class BlogMessages(_id: BSONObjectID, userName: String, message: String, _date: Date)