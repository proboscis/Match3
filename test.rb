classPath = "./common/target/scala-2.10/classes"
Dir.glob(classPath + "/**/").each{|dir|
	puts dir
}