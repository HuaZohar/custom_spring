package cn.hq.spring;

import cn.hq.service.impl.StudentServiceImpl;

public class StudentServiceFactory {
	
	public static StudentServiceImpl createStudentBean(){
		return new StudentServiceImpl();
	}
	
	public StudentServiceImpl createStudentBean2(){
		return new StudentServiceImpl();
	}
}
