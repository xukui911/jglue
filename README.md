# jglue

### Introduction
The script engine implemented in Java is purely interpreted and executed. It is syntactically compatible with js and easy to use; 
Small size (the compiled jar package is only 350K+); Its function and efficiency are completely superior to the existing java interpretive
script and expression engine。 Seamlessly integrates with the host java language in terms of rules and decisions. It can also be used as the
back-end business function extension engine of the low-code platform
     

### Software Architecture


### Installation Tutorial

1. After downloading the project, you can directly compile the jar package according to the source code, or find the compiled jar package
   in the target directory under the project

2. Directly introduce the jglue package into the target project

### Instructions for use

 #### 1.First example：hellojglue
    
 ##### Integration of analysis and execution：
```
    JglueEngine engine = new JglueEngine();
    JGlueContext context = new JSimpleContext();\n
    context.set("name", "jglue");
    engine.execRuntime("print('hello,' +name+ '！！')", context);
```
	
  ##### Analysis execution separation  
```
    JglueEngine engine = new JglueEngine();
    engine.addContent("func hellojglue() {print('hello,'+name+'!!');}");

    JGlueContext context = new JSimpleContext();
    context.set("name", "jglue");
    engine.execFunc("hellojglue", context);
```

  Through file definition (the format code is clear and beautiful): create a new file under classpath: http://xw/lue/test.glue

   Type script content：
        
```
       func helloJglue() {
            print('hello,'+name+'!!');
        }
```

    
 Execute script code in the following way：
   

```
    JglueEngine engine = new JglueEngine();
    engine.loadFile('com/xw/glue/test.glue');


    JGlueContext context = new JSimpleContext();
    context.set("name", "jglue");
    engine.execFunc("helloJglue", context);
```

    
#### 2.  Basic grammar
    
#####  Define Number： 
       var a = 1；
 
#####  Define String
       var b = 'b';

#####  Define Object： 
      var a = 'a';
       var b = 'b';
       var c = {a: a, b: b, c: 'c', d: 4}; 
       var d = c.a;
       var e = c['b'];
       var f = 'c';
       var g = c[e];       
       
       print('a=' + a);
       print('b='+b);
       print('c='+c);
       print('d='+d);
       print('e='+e);
       print('f='+f);
       print('g='+g);       

#####  Define Array：
        var a = 'a';
        var b = 'b';
       var c = {a: a, b: b, c: 'c', d: 4};
       var d = [a, b, c, 4, '5'];
       var e = d[0];
       var f = 1;
       var g = d[f];
       var h = d[2].c;
       var i = d[2]['d'];
       
       print(a);
       print(b);
       print(c);
       print(d);
       print(e);
       print(g);
       print(h);
       print(i);
    
    
#####  Define Function(Use keywords func or function)：   
```
       func hello() {
         var a = 1;    
         var b = 'b';  
         var c = {a: a, b: b, c: 'c', d: 4};
         var d = [a, b, c, 4, '5'];
       } 
```
###### if
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
   
###### while
    func helloWhile() {
        var i=10;
        while(i>0) {
            print(i);
            i--;
        }
    }
###### for
    func helloFor() {
        //common for
        for(var i=0;i<10;i++) {
            print(i);
        }
        //map
        var map = {a: 'a', b: 'b'};
        for(key,val of map) {
            print(key+'='+val);
        }

        //list
        var list = ['a',1,2,3, 'b'];
        for(val,index of list) {
            print('list['+index+']='+val);
        }
    }
###### switch
    func helloSwitch() {
        var a = 1;
        switch(a) {
            case 0: print(0); break;
            case 1: print(1); break;
            default: print('defalut'); break;
        }
    }
###### try/catch/finally
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
###### Note
```
func helloDesc() {
  var a = 1; // this is note
  // this is note
  var b = 2;
  /*
    this is note
   */
  var c = 3;
}
```
    
#### 3.Advanced grammar

###### Execute Function
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

java execute：
JGlueContext context = new JSimpleContext();
engine.execFunc("funcA", context);

``` 
###### Subfunction definition
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

java execute：
JGlueContext context = new JSimpleContext();
engine.execFunc("parent", context);

```
###### Anonymous function
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
     
Java Execute：
JGlueContext context = new JSimpleContext();
engine.execFunc("parent", context);

```

###### Function passed as parameter
```
func funcA() {
  	funcB((a) => {print(a);}, (b) => {return 2*b;});
}

func funcB(fa, fb) {
  	fa(1);
  	print(fb(2));
}

Java Execute：
JGlueContext context = new JSimpleContext();
engine.execFunc("funcA", context);
```
###### Supports closures
```
func funcA() {
  	var fa = funcB();
  	print(fa(100));
  	// call chaining
  	print(funcB()(200));
}
func funcB() {
  	var a = 1;
  	return (x) => { return a+x;};
}

Java Execute：
GlueContext context = new JSimpleContext();
engine.execFunc("funcA", context);
```

###### Object-oriented support
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

Java Execute：
GlueContext context = new JSimpleContext();
engine.execFunc("funcA", context);
```
#### 4.Common system functions

###### Static function (customizable and extensible)
```
func funtA() {
  print('hello, jglue');   //print
  len('hello, jglue')      //get length

  Math.random();
  Math.floor(5.5);
  Math.min(5, 6);
  Math.max(5, 6);
}

Java Execute：
GlueContext context = new JSimpleContext();
engine.execFunc("funcA", context);

```
###### Object function (customizable and extensible)

```
func funtA() {
  // execute by Thread
  new Thread((t) => {
    print(t.name);
    print(t.id);
  }).start();
  
  //String
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
  print(arr.indexOf(2));   //Get the location of the specified element
  
  //Promise
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
  print(promise.get());  //Get the final result synchronously

}

Java Execute：
GlueContext context = new JSimpleContext();
engine.execFunc("funcA", context);

```
#### 5.Custom function
```
        //First define the print function
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

        //unit testing
        public void testAddFunc() {
            JglueEngine engine = new JglueEngine();
	    JGlueContext context = new JSimpleContext();
	    engine.addFunc(new DemoPrintFunc());
	    context.set("paramOne", "this is addFunc UnitTest！！");
	    engine.execFunc("demoPrint", context);
            
            //References in other functions   
            engine.execRuntime("()=>{demoPrint('hello, I\\'m demoPrint!!');}()", context);
	}
        
        //Output results
        this is addFunc UnitTest！！
        hello, I'm demoPrint!!

```

#### 6.Multi-thread security verification
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
				System.out.println("Thread "+a+" result is "+obj);
			}, String.valueOf(a)).start();
		}
                // wait
		LockSupport.parkNanos(1000000000l);
	}

//Output：
hread 9 result is 54
Thread 18 result is 63
Thread 7 result is 52
Thread 13 result is 58
Thread 12 result is 57
Thread 2 result is 47
Thread 11 result is 56
Thread 8 result is 53
Thread 4 result is 49
Thread 1 result is 46
Thread 17 result is 62
Thread 15 result is 60
Thread 3 result is 48
Thread 14 result is 59
Thread 0 result is 45
Thread 5 result is 50
Thread 10 result is 55
Thread 19 result is 64
Thread 16 result is 61
Thread 6 result is 51

```

#### 7.Import use

Create two script files at classpath
com/xw/glue/test1.glue
com/xw/glue/test2.glue
```
//test1.glue

import com/xw/glue/test2.glue as test;

func testImport() {
	print(test.getFibonacci(10));
}

//test2.glue
function getFibonacci(n) {  
  var fibarr = [];
  var i = 0;
  while(i<n) {
    if(i<=1) {
      fibarr.push(i);
    }else{
      fibarr.push(fibarr[i-1] + fibarr[i-2]);
    }
    i++;
  }
  return fibarr;
}    

//Unit testing
public void testImport() {
		JSimpleContext context = new JSimpleContext();
		JglueEngine engine = new JglueEngine();
		engine.loadFile("com/xw/glue/test1.glue");
		
		engine.execFunc("testImport", context);
	}
```
