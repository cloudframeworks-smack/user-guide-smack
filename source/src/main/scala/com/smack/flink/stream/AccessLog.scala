package com.smack.flink.stream

import scala.util.matching.Regex

case class AccessBaseLog(
	namespace: String,
	serverName: String,
	log: String)
case class AccessStatusLog(
	namespace: String,
	serverName: String,
	status: Int,
	start_time: String,
	end_time: String,
	num: Int)
case class AccessRequestLog(
	namespace: String,
	serverName: String,
	request: String,
	start_time: String,
	end_time: String,
	num: Int,
	avg_time: Double)

case class AccessLog(
	namespace: String,
	serverName: String,
	remoteAddr: String,
	remoteUser: String,
	timeLocal: String,
	request: String,
	status: Int,
	bodySize: Int,
	httpReferer: String,
	httpUserAgent: String,
	httpXForwardedFor: String,
	requestLength: Int,
	upstreamResponseTime: Double,
	upstreamAddr: String)

object AccessLog {
	//val PARTTERN: Regex = """(.*)\s(.*)\s(\d+.\d+.\d+.\d+)\s-\s(.*)\s\[(.+)\]\s\"(.*)\"\s(\d{3,})\s(\d+)\s\"([^\s]*)\"\s\"([^\s]*)\"\s\"(.*?)\"\s(.*)\s(.*)\s(.*)$"""r
	val PARTTERN: Regex = """(.*)\s(.*)\s(\d+.\d+.\d+.\d+)\s-\s(.*)\s\[(.+)\]\s\"(.*)\"\s(\d{3,})\s(\d+)\s\"(.*)\"\s\"(.*)\"\s\"(.*?)\"\s(.*)\s(.*)\s(.*)$"""r
	def isValidateLogLine(line: String): Boolean = {
		val options = PARTTERN.findFirstMatchIn(line)
		if (options.isEmpty) {
			false
		} else {
			true
		}
	}

	def parseLogLine(line: String): AccessLog = {
		if (!isValidateLogLine(line)) {
			throw new IllegalArgumentException("参数格式异常")
		}
		val options = PARTTERN.findFirstMatchIn(line)
		val matcher = options.get
		AccessLog(
			matcher.group(1),
			matcher.group(2),
			matcher.group(3),
			matcher.group(4),
			matcher.group(5),
			matcher.group(6),
			matcher.group(7).toInt,
			matcher.group(8).toInt,
			matcher.group(9),
			matcher.group(10),
			matcher.group(11),
			matcher.group(12).toInt,
			matcher.group(13).toDouble,
			matcher.group(14))
	}
}