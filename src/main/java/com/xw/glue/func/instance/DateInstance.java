package com.xw.glue.func.instance;

import java.util.Calendar;
import java.util.Date;

import com.xw.glue.context.JGlueContext;
import com.xw.glue.func.AbstractInstFunc;
import com.xw.glue.func.InstanceFunc;
import com.xw.glue.func.factory.AbstractInsFuncFactory;
import com.xw.glue.object.JglueClass;

public class DateInstance extends InstanceFunc {
	private static AbstractInsFuncFactory<JDate> factory;
	static {
		factory = new AbstractInsFuncFactory<JDate>();
		factory.add(new GetFullYearFunc());
		factory.add(new GetFullYearFunc("getYear"));
		factory.add(new GetMonthFunc());
		factory.add(new GetDateFunc());
		factory.add(new ToStringFunc());
		factory.add(new GetTimeFunc());
		
	}
		
	public DateInstance() {
		super("Date");
		addParam("param");
	}

	@Override
	public JglueClass<?> build(JGlueContext context, Object[] objs) {
		Object obj = null;
		if(objs!=null && objs.length > 0) {
			obj = objs[0];
		}
		return new DateClass(context, obj);
	}
	
	static class JDate {
		Calendar calendar = Calendar.getInstance();
		JDate(Object param) {
			if (param ==null) {
				calendar.setTime(new Date());
			} else {
				if (param instanceof Number) {
					calendar.setTime(new Date((Long)param));
				} else {
					calendar.setTime(new Date());
				}
			}
		}
		
		public Calendar getCalendar() {
			return calendar;
		}
		
		public int getYear() {
			return  calendar.get(Calendar.YEAR);
		}
		
		public int getMonth() {
			return  calendar.get(Calendar.MONTH) + 1;
		}
		
		public int getDate() {
			return calendar.get(Calendar.DATE);
		}
		
		public int getHour() {
			return  calendar.get(Calendar.HOUR_OF_DAY);
		}
		
		public int getMinute() {
			return  calendar.get(Calendar.MINUTE);
		}
		
		public int getSecond() {
			return calendar.get(Calendar.SECOND);
		}
		
		public long getTime() {
			return calendar.getTimeInMillis();
		}
		
		public String toString() {
			return  getYear()+"-" + getStrCal(getMonth()) +'-' + getStrCal(getDate()) +" "
			  + getStrCal(getHour()) + ":" + getStrCal(getMinute()) + ":" + getStrCal(getSecond());
		}
		
		private String getStrCal(int val) {
			if(val < 10) {
				return "0" + val;
			} else {
				return String.valueOf(val);
			}
		}
	}
	


	static class DateClass extends JglueClass<JDate> {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public DateClass(JGlueContext context, Object param) {
			super(context, new JDate(param));
		}

		@Override
		public AbstractInstFunc<JDate> buildFunc(String funcName) {
			return factory.getFunc(funcName);
		}
		
		public String toString() {
			return instance.toString();
		}
	}
	
	static class GetFullYearFunc extends AbstractInstFunc<JDate> {
		GetFullYearFunc() {
			this("getFullYear");
		}
		
		GetFullYearFunc(String funcName) {
			super(funcName);
		}
		@Override
		public Object exec(JGlueContext context, Object[] objs, JDate jdate) {
			return jdate.getYear();
		}
		
	}
	
	static class GetMonthFunc extends AbstractInstFunc<JDate> {
		GetMonthFunc() {
			super("getMonth");
		}
		
		@Override
		public Object exec(JGlueContext context, Object[] objs, JDate jdate) {
			return jdate.getMonth();
		}
		
	}
	
	static class GetDateFunc extends AbstractInstFunc<JDate> {
		GetDateFunc() {
			super("getDate");
		}
		
		@Override
		public Object exec(JGlueContext context, Object[] objs, JDate jdate) {
			return jdate.getDate();
		}
		
	}
	
	static class GetTimeFunc extends AbstractInstFunc<JDate> {
		GetTimeFunc() {
			super("getTime");
		}
		
		@Override
		public Object exec(JGlueContext context, Object[] objs, JDate jdate) {
			return jdate.getTime();
		}
		
	}
	
	static class ToStringFunc extends AbstractInstFunc<JDate> {
		ToStringFunc() {
			super("toString");
		}
		
		@Override
		public Object exec(JGlueContext context, Object[] objs, JDate jdate) {
			return jdate.toString();
		}
		
	}
}
