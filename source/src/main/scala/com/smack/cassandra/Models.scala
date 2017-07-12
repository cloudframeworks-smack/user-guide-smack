package com.smack.cassandra

import java.util.UUID
import java.util.Date

case class Event(
	namespace: String,
	servername: String,
	log: String)

case class NginxLog(
	namespace: String,
	servername: String,
	remoteaddr: String,
	remoteuser: String,
	timelocal: String,
	request: String,
	status: Int,
	bodysize: Int,
	httpreferer: String,
	httpuseragent: String,
	httpxforwardedfor: String,
	requestlength: Int,
	upstreamresponsetime: Double,
	upstreamaddr: String)

case class StatusRealStatics(
	namespace: String,
	servername: String,
	status: Int,
	start_time: String,
	end_time: String,
	num: Int)

case class RequestRealStatics(
	namespace: String,
	servername: String,
	request: String,
	start_time: String,
	end_time: String,
	num: Int,
	avg_time: Double)

case class RequestBathStatics(
	namespace: String,
	servername: String,
	request: String,
	num: Int)


