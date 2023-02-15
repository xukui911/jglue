# jglue

### 介绍
java实现的脚本引擎，纯解释执行。语法上最大程度兼容js，使用简单；体积小巧（编译后的jar包只有350K+）；功能和效率全面超越现有的java解释型脚本以及表达式引擎
（在百万次循环执行中，比qlexpress执行效率有倍数提升）。在规则以及决策方面与宿主java语言无缝集成。也可以作为低代码平台后端业务功能扩展引擎
     

### 软件架构
软件架构说明

### 安装教程

1.  下载项目后，可直接根据源码编译出jar包，或者在项目下的target目录下找到已经编译好的jar包
2.  在目标项目中直接引入jglue包

### 使用说明

 #### 1.第一个案例：hellojglue
    
 ##### 解析执行一体：
```
    JglueEngine engine = new JglueEngine();
    JGlueContext context = new JSimpleContext();\n
    context.set("name", "jglue");
    engine.execRuntime("print('hello,' +name+ '！！')", context);
```
	
  ##### 解析执行分离  
```
    JglueEngine engine = new JglueEngine();
    engine.addContent("func hellojglue() {print('hello,'+name+'!!');}");

    JGlueContext context = new JSimpleContext();
    context.set("name", "jglue");
    engine.execFunc("hellojglue", context);
```

  通过文件定义(格式化代码清晰美观)：classpath下新建文件：com/xw/glue/test.glue
  键入脚本内容：
        
```
       func helloJglue() {
            print('hello,'+name+'!!');
        }
```

    
 通过下面方式执行脚本代码：
   

```
    JglueEngine engine = new JglueEngine();
    engine.loadFile('com/xw/glue/test.glue');


    JGlueContext context = new JSimpleContext();
    context.set("name", "jglue");
    engine.execFunc("helloJglue", context);
```

    
#### 2.  基本语法
    
#####  定义数字： 
       var a = 1；
 
#####  定义字符串
       var b = 'b';

#####  定义对象： 
       var c = {a: a, b: b, c: 'c', d: 4};   

#####  定义数组： 
       var d = [a, b, c, 4, '5'];
    
    
#####  方法定义(关键字func或者function)：   
```
       func hello() {
         var a = 1;    
         var b = 'b';  
         var c = {a: a, b: b, c: 'c', d: 4};
         var d = [a, b, c, 4, '5'];
       } 
```
###### 条件判断if
    func helloIf() {
        var a = 1;
        if(a>0) {
            print('a > 0');
        } else if(a==0){
            print('a==0');
        }else {
            print('a < 0');
        }
    }
   
###### 循环结构while
    func helloWhile() {
        var i=10;
        while(i>0) {
            print(i);
            i--;
        }
    }
###### 循环结构for
    func helloFor() {
        //普通for循环
        for(var i=0;i<10;i++) {
            print(i);
        }
        //遍历map
        var map = {a: 'a', b: 'b'};
        for(key,val of map) {
            print(key+'='+val);
        }

        //遍历list
        var list = ['a',1,2,3, 'b'];
        for(val,index of list) {
            print('list['+index+']='+val);
        }
    }
###### 选择语句switch
    func helloSwitch() {
        var a = 1;
        switch(a) {
            case 0: print(0); break;
            case 1: print(1); break;
            default: print('defalut'); break;
        }
    }
###### 异常处理try/catch/finally
     func helloTryCatch() {
        var a = 1;
        try{
            var b = a.a;
            print('execute success：'+b); 
        }catch(e) {
           print('execute error：'+e); 
        }finally {
            print('excute finally');
        }
    }
###### 注释使用
```
func helloDesc() {
  var a = 1; //这是注释
  //这是注释
  var b = 2;
  /*
    这里也可以是注释
   */
  var c = 3;
}
```
    
#### 3.高级语法部分

###### 函数调用
```
func funcA() {
    funcB();
    funcC('funcC');
}

func funcB() {
    print('hello, funcB');
}

func funcC(c) {
    print('hello,' + c);
}

调用代码：
JGlueContext context = new JSimpleContext();
engine.execFunc("funcA", context);

``` 
###### 子函数定义
```
func parent() {
     func childA() {
  	print('my name is ChildA');
     };
     var childB() {
  	print('my name is ChildB');
     };
     childA();
     childB();   
}

调用代码：
JGlueContext context = new JSimpleContext();
engine.execFunc("parent", context);

```
###### 匿名函数
```
func parent() {
  	var childA = () => {
  		print('my name is ChildA');
  	};
  	var childB = () => {
  		print('my name is ChildB');
  	};
  	childA();
  	childB();
}
     
调用代码：
JGlueContext context = new JSimpleContext();
engine.execFunc("parent", context);

```

###### 函数作为参数传递
```
func funcA() {
  	funcB((a) => {print(a);}, (b) => {return 2*b;});
}

func funcB(fa, fb) {
  	fa(1);
  	print(fb(2));
}

调用代码：
JGlueContext context = new JSimpleContext();
engine.execFunc("funcA", context);
```
###### 支持闭包
```
func funcA() {
  	var fa = funcB();
  	print(fa(100));
  	//支持链式调用
  	print(funcB()(200));
}
func funcB() {
  	var a = 1;
  	return (x) => { return a+x;};
}

调用代码：
GlueContext context = new JSimpleContext();
engine.execFunc("funcA", context);
```

###### 面向对象支持：
```
func Person(name, age) {
  this.name = name;
  this.age = age;
  this.say = () => {
    print('my name is ' + this.name +',i\'m '+age);
  };
}

func funcA() {
  var lilei = new Person('lilei', 20);
  print(lilei.name + ' say:');
  lilei.say();
  
  var hanmeimei = new Person('hanmeimei', 19);
  print(hanmeimei.name + ' say:');
  hanmeimei.say()
}

调用代码：
GlueContext context = new JSimpleContext();
engine.execFunc("funcA", context);
```
#### 4.常用系统函数

###### 静态函数（可自定义扩展）
```
func funtA() {
  print('hello, jglue');   //打印
  len('hello, jglue')      //获取字符串长度

  Math.random();
  Math.floor(5.5);
  Math.min(5, 6);
  Math.max(5, 6);
}

调用代码：
GlueContext context = new JSimpleContext();
engine.execFunc("funcA", context);

```
###### 对象函数（可自定义扩展）

```
func funtA() {
  //启动新线程执行任务
  new Thread((t) => {
    print(t.name);
    print(t.id);
  }).start();
  
  //String方法
  print('abc'.length());
  print('abc'.substring(0,1));
  print('abc'.split('b'));
  print('abc'.indexOf('b'));
  print('abc'.trim());
  print('abc'.startsWith('a'));
  print('abc'.charAt(0));
  
  //Date
  var date = new Date();
  print(date.getTime());
  print(date.getYear());
  print(date.getMonth());
  print(date.getDate());
  print(date.toString());
  
  //array
  var arr = [];
  print(arr.push(1));
  print(arr[0]);
  print(arr.push(2));
  print(arr.size());
  print(arr.length);
  print(arr.indexOf(2));   //获取指定元素所在位置
  
  //Promise异步
  var promise = new Promise((r, j)=>{r(123);}).then(
    (data) => {
      print(data);
      return data + 100;
    }
  ).then(
    (data) => {
      print(data);
      return data + 100;
    }
  );
  print(promise.get());  //同步获取最终结果

}

调用代码：
GlueContext context = new JSimpleContext();
engine.execFunc("funcA", context);

```
#### 5.自定义函数
```
        //首先定义打印函数
	public class DemoPrintFunc extends AbstractFunc {
		public DemoPrintFunc() {
			super("demoPrint");
			addParam("demoPrint");
		}
		
		@Override
		public Object exec(JGlueContext var1, Object[] var2) {
			System.out.print(var2[0]);
			return null;
		}
	}

        //单元测试
        public void testAddFunc() {
            JglueEngine engine = new JglueEngine();
	    JGlueContext context = new JSimpleContext();
	    engine.addFunc(new DemoPrintFunc());
	    context.set("paramOne", "this is addFunc UnitTest！！");
	    engine.execFunc("demoPrint", context);
            
            //其他函数中引用   
            engine.execRuntime("()=>{demoPrint('hello, I\\'m demoPrint!!');}()", context);
	}
        
        //输出结果
        this is addFunc UnitTest！！
        hello, I'm demoPrint!!

```
#### 6.效率展示

###### 简单数学表达式计算（单元测试）
```
public void testQLConst() throws Exception {
		//String express = "c=a+b+d+aa(aa(b,aa(1,9)),3);return c;function aa(int a, int b) {return a+b}";
		String express = "1000+100.0*99-(600-3*15)%(((68-9)-3)*2-100)+10000%7*71";
		ExpressRunner runner = new ExpressRunner(true, false);
    	IExpressContext expressContext = new DefaultContext();
    	Object obj = runner.execute(express, expressContext, null, true, false);
		 long start = System.currentTimeMillis();
	        for(int i=0;i<1000000;i++) {
	        	obj = runner.execute(express, expressContext, null, true, false);
	        }
	     long end = System.currentTimeMillis();
	     System.out.println("testQL耗时："+(end-start) +"ms,执行结果"+obj);
	}
	
	public void testJglueConst() {		
		String express = "1000+100.0*99-(600-3*15)%(((68-9)-3)*2-100)+10000%7*71";
		Express jExpress = new JGlueExpress(express);
		JGlueContext context = new JSimpleContext();
		Object obj = null;
		obj = jExpress.execute(context);
		 long start = System.currentTimeMillis();
	        for(int i=0;i<1000000;i++) {
	        	context.set("b", i);
	        	obj = jExpress.execute(context);
	        }
	        long end = System.currentTimeMillis();
	        System.out.println("testJglueConst耗时："+(end-start) +"ms,执行结果"+obj);
	}
    //执行结果：
      testQL耗时：1399ms,执行结果11181.0
      testJglueConst耗时：121ms,执行结果11181.0
```   
###### 简单函数执行（单元测试）
```
public void testQLJava() throws Exception {
		String express = "c=a+b+d+aa(aa(b,aa(1,9)),3);return c;function aa(int a, int b) {return a+b}";
		ExpressRunner runner = new ExpressRunner();
    	IExpressContext expressContext = new DefaultContext();
    	expressContext.put("d", 70);
    	expressContext.put("a", 10);
    	expressContext.put("b", 20);
    	Object obj = runner.execute(express, expressContext, null, true, false);
		 long start = System.currentTimeMillis();
	        for(int i=0;i<1000000;i++) {
	        	expressContext.put("b", i);
	        	obj = runner.execute(express, expressContext, null, true, false);
	        }
	        long end = System.currentTimeMillis();
	        System.out.println("testQLJava耗时："+(end-start) +",执行结果"+obj);
	}
	
	public void testJglue() {		
		String express = "func a() {c=a+b+d+aa(aa(b,aa(1,9)),3);return c;} func aa(a,b) {return a+b;}";
		JglueEngine engine = new JglueEngine();
		engine.addContent(express);
		JGlueContext context = new JSimpleContext();
		context.set("d", 70);
		context.set("a", 10);
		context.set("b", 20);
		Object obj = null;
		
		obj = engine.execFunc("a", context);
		 long start = System.currentTimeMillis();
	        for(int i=0;i<1000000;i++) {
	        	context.set("b", i);
	        	obj = engine.execFunc("a", context);
	        }
	        long end = System.currentTimeMillis();
	        System.out.println("jgule耗时："+(end-start) +"ms,执行结果"+obj);
	}

    //执行结果：
      testQLJava耗时：1182ms,执行结果2000091
      jgule耗时：623ms,执行结果2000091
```

#### 6.多线程安全验证
 ```
    public void testThread() {
		String content = "func a(d) {var a=10; var b=20; var c=a+b+d+aa(aa(2,aa(1,9)),3);return c;} func aa(a,b) {return a+b;}";
		JglueEngine engine = new JglueEngine();
		engine.addContent(content);
		
		for (int i=0;i<20;i++) {
			final int a = i;
			new Thread(()-> {
				JGlueContext context = new JSimpleContext();
				context.set("d", a);
				Object obj = engine.execFunc("a", context);
				System.out.println("线程"+a+"执行结果"+obj);
			}, String.valueOf(a)).start();
		}
                //让主线程稍微等待一会
		LockSupport.parkNanos(1000000000l);
	}

     //输出结果：
线程9执行结果54
线程18执行结果63
线程7执行结果52
线程13执行结果58
线程12执行结果57
线程2执行结果47
线程11执行结果56
线程8执行结果53
线程4执行结果49
线程1执行结果46
线程17执行结果62
线程15执行结果60
线程3执行结果48
线程14执行结果59
线程0执行结果45
线程5执行结果50
线程10执行结果55
线程19执行结果64
线程16执行结果61
线程6执行结果51

```

### 参与贡献

1.  Fork 本仓库
2.  新建 Feat_xxx 分支
3.  提交代码
4.  新建 Pull Request


### 特技

1.  使用 Readme\_XXX.md 来支持不同的语言，例如 Readme\_en.md, Readme\_zh.md
2.  Gitee 官方博客 [blog.gitee.com](https://blog.gitee.com)
3.  你可以 [https://gitee.com/explore](https://gitee.com/explore) 这个地址来了解 Gitee 上的优秀开源项目
4.  [GVP](https://gitee.com/gvp) 全称是 Gitee 最有价值开源项目，是综合评定出的优秀开源项目
5.  Gitee 官方提供的使用手册 [https://gitee.com/help](https://gitee.com/help)
6.  Gitee 封面人物是一档用来展示 Gitee 会员风采的栏目 [https://gitee.com/gitee-stars/](https://gitee.com/gitee-stars/)
