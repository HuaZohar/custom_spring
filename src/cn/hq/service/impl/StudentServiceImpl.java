package cn.hq.service.impl;

import cn.hq.customspring.HqResource;
import cn.hq.dao.StudentDao;
import cn.hq.service.StudentService;

public class StudentServiceImpl implements StudentService{


	@HqResource
	private StudentDao studentDao;
	
	public void setStudentDao(StudentDao studentDao) {
		this.studentDao = studentDao;
	}	
	
	public void save() throws Exception {
		System.out.println("studentServiceImpl==>save()");
		studentDao.add();
	}

	public void init() throws Exception {
		System.out.println("studentServiceImpl==>init()");
	}

	public void destroy() throws Exception {
		System.out.println("studentServiceImpl==>destroy()");
		
	}
	
}
