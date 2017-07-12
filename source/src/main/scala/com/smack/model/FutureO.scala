package com.smack.model

import concurrent.{ Future, ExecutionContext }

case class FutureO[+A](future: Future[Option[A]]) extends AnyVal {
	def flatMap[B](f: A => FutureO[B])(implicit ec: ExecutionContext): FutureO[B] = {
		FutureO {
			future.flatMap { optA =>
				optA.map { a =>
					f(a).future
				} getOrElse Future.successful(None)
			}
		}
	}

	def map[B](f: A => B)(implicit ec: ExecutionContext): FutureO[B] = {
		FutureO(future.map(_ map f))
	}
}