package cn.hq.test;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import cn.hq.customspring.HqClasspathXmlApplicationContext;
import cn.hq.service.StudentService;

public class springMain {
	
	@Test
	public void springTest() {
//		原始spring容器
//		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("beans.xml");
//		StudentService studentService =  (StudentService) applicationContext.getBean("studentService");
//		try {
//			studentService.save();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		
//		自定义spring容器
//		HqClasspathXmlApplicationContext hqApplicationContext = new HqClasspathXmlApplicationContext("beans.xml");
//		StudentService studentService =  (StudentService) hqApplicationContext.getBean("studentService");
//		try {
//			studentService.save();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		
//		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("beans.xml");
//		StudentService studentService =  (StudentService) applicationContext.getBean("studentService3");
//		try {
//			studentService.save();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		
//		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("beans.xml");
//		StudentService studentService = (StudentService)applicationContext.getBean("studentService");
//		try {
//			studentService.save();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		HqClasspathXmlApplicationContext hqApplicationContext = new HqClasspathXmlApplicationContext("beans.xml");
		StudentService studentService = (StudentService)hqApplicationContext.getBean("studentService");
		try {
			studentService.save();
		} catch (Exception e) {
			e.printStackTrace();	
		}
	}
}
