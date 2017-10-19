package cn.hq.customspring;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * �Զ���ע����
 */
@Retention(RetentionPolicy.RUNTIME) //����ʱ��
@Target({ElementType.FIELD,ElementType.METHOD}) //ע���������ֶ��Ϻͷ�����
public @interface HqResource {
	public String name() default "";
}
