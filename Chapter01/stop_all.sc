#!/usr/bin/env amm

import ammonite.ops._
import ammonite.ops.ImplicitWd._

val res = %%('docker, "ps")
val services=res.out.lines.tail.map(t => t.split("\\s").head -> t.split("\\s").drop(1).dropWhile(_.isEmpty).head)
services.foreach{
  service =>
    println(s"Stopping ${service._2}")
    %%('docker, s"stop",service._1)

}
