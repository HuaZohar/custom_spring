package cn.hq.customspring;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.ConvertUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.XPath;
import org.dom4j.io.SAXReader;

/**
 * 自定义的spring容器
 *
 */
public class HqClasspathXmlApplicationContext {
	private List<BeanDefinition> beanDefinitions = new ArrayList<BeanDefinition>();
	private Map<String,Object> singletons = new HashMap<String,Object>();
	
	public HqClasspathXmlApplicationContext(String filename) {
		this.readXML(filename);
		this.instanceBeans();
		this.annotationInject();
		this.injectObject();
	}

	/**
	 * 自定义注解注入
	 */
	private void annotationInject() {
		for(String beanName : singletons.keySet()) {
			Object bean = singletons.get(beanName);
			if(bean!=null) {
				try {
					PropertyDescriptor[] ps = Introspector.getBeanInfo(bean.getClass()).getPropertyDescriptors();
					
					//setter方法上有注解
					for(PropertyDescriptor propertyDescriptor : ps) {
						Method setter = propertyDescriptor.getWriteMethod();
						if(setter!=null && setter.isAnnotationPresent(HqResource.class)) { //setter上有HqResource注解
							HqResource resource = setter.getAnnotation(HqResource.class);
							Object value = null;
							if(resource.name() !=null&& !"".equals(resource.name())) {
								value = singletons.get(resource.name());
							}else {
								value = singletons.get(propertyDescriptor.getName());
								if(value == null) {
									for(String key : singletons.keySet()) {
										//判断类型是否相同
										if(propertyDescriptor.getPropertyType().isAssignableFrom(singletons.get(key).getClass())) {
											value = singletons.get(key);
											break;
										}
									}
								}
							}
							setter.setAccessible(true);
							setter.invoke(bean, value);							
						}
					}

					//属性上有注解
					Field[] fields = bean.getClass().getDeclaredFields();
					for(Field field: fields) {
						if(field.isAnnotationPresent(HqResource.class)) {
							HqResource resource = field.getAnnotation(HqResource.class);
							Object value = null;
							if(resource.name() !=null&& !"".equals(resource.name())) {
								value = singletons.get(resource.name());
							}else {
								value = singletons.get(resource.name());
								if(value == null) {
									for(String key : singletons.keySet()) {
										//判断类型是否相同
										if(field.getType().isAssignableFrom(singletons.get(key).getClass())) {
											value = singletons.get(key);
											break;
										}
									}
								}
							}
							field.setAccessible(true);
							field.set(bean, value);
						}
					}
					
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	//为bean对象的属性注入值
	private void injectObject() {
		for(BeanDefinition beanDefinition : beanDefinitions) {
			Object bean = singletons.get(beanDefinition.getId());
			if(bean!=null) {
				try {
					PropertyDescriptor[] ps = Introspector.getBeanInfo(bean.getClass()).getPropertyDescriptors();
					for(PropertyDefinition propertyDefinition : beanDefinition.getPropertys()) {
						for(PropertyDescriptor propertyDescriptor : ps) {
							if(propertyDefinition.getName().equals(propertyDescriptor.getName())) {
								Method setter = propertyDescriptor.getWriteMethod(); //获取属性的set方法
								if(setter != null) {
									Object value = null;
									if(propertyDefinition.getRef()!=null&&!"".equals(propertyDefinition.getRef().trim())) {
										value = singletons.get(propertyDefinition.getRef());
									}else {
										value = ConvertUtils.convert(propertyDefinition.getValue(), propertyDescriptor.getPropertyType());
									}
									setter.setAccessible(true); // 设置为true 以防属性为private
									setter.invoke(bean, value); //将引用对象注入到属性
								}
							}
						}
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	//完成Bean实例化
	private void instanceBeans() {
		for(BeanDefinition beanDefinition : beanDefinitions) {
				try {
					if(beanDefinition.getClassName() != null && !"".equals(beanDefinition.getClassName().trim())) {
						singletons.put(beanDefinition.getId(), Class.forName(beanDefinition.getClassName()).newInstance());
					}
				} catch (Exception e) {
					e.printStackTrace();
				} 
		}
	}

	
	//读取XML文件中的bean  
	@SuppressWarnings("unchecked")
	private void readXML(String filename) {	
		SAXReader saxReader = new SAXReader();
		Document document = null;
		
		try {
			URL xmlpath = this.getClass().getClassLoader().getResource(filename);
			document = saxReader.read(xmlpath);
			Map<String,String> nsMap = new HashMap<String,String>();
			nsMap.put("ns", "http://www.springframework.org/schema/beans"); //加入命名空间
			XPath xPath = document.createXPath("//ns:beans/ns:bean"); //创建beans和bean查询路劲
			xPath.setNamespaceURIs(nsMap); //设置命名空间
			List<Element> beans = xPath.selectNodes(document); //获取文档下所有的bean节点
			for(Element element:beans) {
				String id = element.attributeValue("id");
				String clazz = element.attributeValue("class");
				BeanDefinition beanDefinition = new BeanDefinition(id,clazz);
				
				//读取bean的属性property节点
				XPath xPathProperty = element.createXPath("ns:property"); //
				xPathProperty.setNamespaceURIs(nsMap); //设置命名空间
				
				List<Element> propertys = xPathProperty.selectNodes(element);
				for(Element property : propertys) {
					String propertyName = property.attributeValue("name");
					String propertyRef = property.attributeValue("ref");
					String propertyValue = property.attributeValue("value");
//					System.out.println("name=" + propertyName + "-----ref=" + propertyRef);
					PropertyDefinition propertyDefinition = new PropertyDefinition(propertyName, propertyRef,propertyValue);
					beanDefinition.getPropertys().add(propertyDefinition);
				}
				
				
				beanDefinitions.add(beanDefinition);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取bean实例
	 * @param beanName
	 * @return
	 */
	public Object getBean(String beanName) {
		return singletons.get(beanName);
	}
	
	
}
