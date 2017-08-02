package com.smack.spark

case class AccessLog(
	namespace: String,
	serverName: String,
	remoteAddr: String,
	request: String,
	status: Int,
	upstreamResponseTime: Double,
	create_time: String)