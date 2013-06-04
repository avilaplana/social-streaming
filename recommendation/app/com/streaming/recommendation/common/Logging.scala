package com.streaming.recommendation.common

import org.slf4j.LoggerFactory


trait Logging {
  private lazy val logger = LoggerFactory.getLogger(this.getClass)

   def debug(msg : => String) : Unit = logger.debug(msg)

   def debug(msg : => String, t : Throwable) : Unit = logger.debug(msg, t)

   def info(msg : => String) : Unit = logger.info(msg)

   def info(msg : => String, t : Throwable) : Unit = logger.info(msg, t)

   def warn(msg : => String) : Unit = logger.warn(msg)

   def warn(msg : => String, t : Throwable) : Unit = logger.warn(msg, t)

   def trace(msg : => String) : Unit = logger.trace(msg)

   def trace(msg : => String, t : Throwable) : Unit = logger.trace(msg, t)

   def error(msg : => String) : Unit = logger.error(msg)

   def error(msg : => String, t : Throwable) : Unit = logger.error(msg, t)
}