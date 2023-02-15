package com.xw.glue.context;

import java.util.HashMap;
import java.util.Map;

public class JGlueMap<V> {
	@SuppressWarnings({"unchecked"})
	Entrys<V>[] eArray = (Entrys<V>[])new Entrys[16];
	
	public JGlueMap() {
		
	}
	
	public V put(String key, V value) {
		int index = (eArray.length - 1) & hash(key);
		Entrys<V> entry = eArray[index];
		if(entry==null) {
			eArray[index] = new Entrys<V>(new Entry<V>(key, value)); 
		} else {
			entry.addEntry(key, value);
		}
		return value;
	}
	
	public V get(String key) {
		int index = (eArray.length - 1) & hash(key);
		Entrys<V> entry = eArray[index];
		if(entry==null) {
			return null;
		}
		return entry.getValue(key);
	}
	
	public boolean containsKey(String key) {
		int index = (eArray.length - 1) & hash(key);
		Entrys<V> entry = eArray[index];
		return entry!=null && entry.getEntry(key)!=null;
	}
	public class Entrys<O> {
		@SuppressWarnings({"unchecked"})
		Entry<O>[] entrys = (Entry<O>[])new Entry[2];
		
		public Entrys(Entry<O> entry) {
			entrys[0] = entry;
		}
		
		public void addEntry(String key, O value) {
			Entry<O> entry = getEntry(key);
			if(entry==null) {
				for(int i=0;i<entrys.length;i++) {
					if(entrys[i] == null) {
						entrys[i] = new Entry<O>(key, value);
					}
				}
			} else {
				entry.value = value;
			}
		}
		
		public Entry<O> getEntry(String key) {
			for(int i=0;i<entrys.length;i++) {
				if(entrys[i]!=null && entrys[i].key.equals(key)) {
					return entrys[i];
				}
			}
			return null;
		}
		
		public O getValue(String key) {
			for(int i=0;i<entrys.length;i++) {
				if(entrys[i]!=null && entrys[i].key.equals(key)) {
					return entrys[i].value;
				}
			}
			return null;
		}
	}
	
	public class Entry<O> {
		String key;
		O value;
		Entry<O> next;
		
		Entry(String key, O value) {
			this.key = key;
			this.value = value;
		}
		
		
		
		public void put(String key, O value) {
			if (key.equals(this.key)) {
				this.value = value;
			} else if(next!=null){
				next.put(key, value);
			} else {
				next = new Entry<O>(key,value);
			}
		}
		
		public O get(String key) {
			if(this.key.equals(key)) {
				return value;
			} else if(next!=null){
				return next.get(key);
			} else {
				return null;
			}
		}
	}
	
	static final int hash(String key) {
        int h;
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    }
	
	public static void main(String[] args) {
		Map<String, JValue> map = new HashMap<String, JValue>();
		//JGlueMap<JValue> map = new JGlueMap<JValue>();
		long start = System.nanoTime();
		for(int i=0;i<100;i++) {
			map.put("abc", new JValue("abc", 123));
			map.put("def", new JValue("def", 456));
			map.put("ghi", new JValue("ghi", 789));
		}
		long end = System.nanoTime();
		System.out.println("耗时："+(end-start)/1000000+"ms");
	}
}
