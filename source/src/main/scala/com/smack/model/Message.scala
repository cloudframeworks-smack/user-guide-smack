package com.smack.model

case class MsgEvent(namespace: String, serverName: String, msg: String)

case class QueryEvent(querytype: String, namespace: String, serverName: String, start: Int, offset: Int)

case class CmdEvent(cmd: String)

case class MsgKafka(topic: String, data: String)

case class CmdMessage(namespace: String, serverName: String, cmd: String, start_time: String, end_time: String)

case class CmdMessageByPeriod(namespace: String, serverName: String, cmd: String, start_time: String, end_time: String, format: String)