#!/usr/bin/env amm

import ammonite.ops._
import ammonite.ops.ImplicitWd._

val res = %%('docker, "images")
val services=res.out.lines.tail.map(t => t.split("\\s").head -> t.split("\\s").filterNot(_.isEmpty).drop(2).head)

val VALIDIMAGES=Set("jenkins", "gitlab/gitlab-ce", "postgres",
  "selenium/standalone-chrome",
"sonarqube", "sonatype/nexus3", "fedora", "ubuntu", "centos")

def keepImage(name:String):Boolean={
  if(name.startsWith("<")) true
  else if(VALIDIMAGES.contains(name)) true
  else name.startsWith("morpheusdb")
}

services.filterNot(s => keepImage(s._1)).foreach{
  service =>
    println(s"Removing image ${service}")
    %%('docker, "rmi", service._2)
}
