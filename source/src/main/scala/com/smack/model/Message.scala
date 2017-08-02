package com.smack.model

case class CmdMessage(namespace: String, serverName: String, cmd: String, start_time: String, end_time: String)

case class CmdMessageByPeriod(namespace: String, serverName: String, cmd: String, start_time: String, end_time: String, format: String)