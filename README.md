In Java, <code>String</code>s (<a href="http://docs.oracle.com/javase/7/docs/api/java/lang/String.html" target="_blank">Java Doc</a>) are immutable objects as documented in the class Java Doc


<blockquote cite="http://docs.oracle.com/javase/7/docs/api/java/lang/String.html">
Strings are constant; their values cannot be changed after they are created.
</blockquote>


Therefore, once a <code>String</code> is created, it cannot be modified.  Yet, Java provides means for modifying strings without creating new instances as shown in this article.  Java also provides means to safeguard against such things and protect the code from malicious intentions.  By modifying strings we are breaking their contract and thus such action is discouraged.


All code listed below is available at: <a href="https://java-creed-examples.googlecode.com/svn/lang/How%20to%20Modify%20a%20String%20Without%20Creating%20a%20New%20One/" target="_blank">https://java-creed-examples.googlecode.com/svn/lang/How to Modify a String Without Creating a New One</a>.  Most of the examples will not contain the whole code and may omit fragments which are not relevant to the example being discussed. The readers can download or view all code from the above link.


This article was inspiered by a <a href="http://vimeo.com/117411144" target="_blank">video</a> posted by Heinz Kabutz (<a href="http://www.javaspecialists.eu/" target="_blank">Homepage</a>) few days ago.  The readers are encouraged to watch this video.


<h2>Modify a String Instance</h2>


Before we see how to modify a <code>String</code> we need to understand how the <code>String</code> object save its value.  The <code>String</code>s were designed to be immutable, thus their values will not change.  The <code>String</code> class makes use of a character (primitive type) array to store its value as shown in the following image.


<a href="http://www.javacreed.com/wp-content/uploads/2015/02/String-Field-Value.png" class="preload" rel="prettyphoto" title="String’s Field value" ><img src="http://www.javacreed.com/wp-content/uploads/2015/02/String-Field-Value.png" alt="String’s Field value" width="1154" height="505" class="size-full wp-image-5315" /></a>


The array is created when the <code>String</code> object is instantiated and this is never modified.  Arrays provide high access speed and are the perfect data structure for situations where the length is known beforehand and when resizing is not required.


Arrays in Java are mutable and Java does not provide or support read-only arrays.  Therefore, if we can access the field <code>value</code>, within the <code>String</code> class, then we can also modify the <code>String</code> content.  Furthermore, we can replace the array with yet another array and thus have a completely different string value.  This can be achieved with reflection.


In Java, classes, methods and fields (also known as properties) are <em>first class objects</em>.  As defined in <em>Structure and Interpretation of Computer Programs</em> by Gerald Jay Sussman and Harry Abelson (<a href="http://mitpress.mit.edu/sicp/full-text/book/book-Z-H-12.html#call%5Ffootnote%5FTemp%5F121" target="_blank">reference</a>), a first class element is an element such that it can be 


<ul>
<li>Saved into variable</li>
<li>Passed as argument to methods</li>
<li>Returned as the result of methods</li>
<li>Included in data structures (as fields in classes, collections and arrays)</li>
</ul>


Java has objects that represents classes, methods and fields and these are: <code>Class</code> (<a href="http://docs.oracle.com/javase/7/docs/api/java/lang/Class.html" target="_blank">Java Doc</a>),  <code>Method</code> (<a href="http://docs.oracle.com/javase/7/docs/api/java/lang/reflect/Method.html" target="_blank">Java Doc</a>) and <code>Field</code> (<a href="http://docs.oracle.com/javase/7/docs/api/java/lang/reflect/Field.html" target="_blank">Java Doc</a>) respectively.  Therefore we can access the <code>String</code>'s field named <code>value</code> and modify it outside the <code>String</code> class by accessing the <code>Field</code> object.


Java supports reflection, which can be used to get access to the <code>String</code>'s <code>value</code> field as shown in the following example.


<pre>
package com.javacreed.examples.lang;

import java.lang.reflect.Field;

public class Example1 {

  public static void main(final String[] args) throws Exception {
    final String s = "Immutable String";
    PrintUtils.print(s);

    final Class&lt;String&gt; type = String.class;
    final Field valueField = type.getDeclaredField("value");
    valueField.setAccessible(true);

    final char[] value = (char[]) valueField.get(s);
    value[0] = 'i';
    value[10] = 's';
    PrintUtils.print(s);

    System.arraycopy("Mutable String".toCharArray(), 0, value, 0, 14);
    PrintUtils.print(s);

    valueField.set(s, "Mutable String".toCharArray());
    PrintUtils.print(s);
  }
}
</pre>


In the above example, we are doing several things.  Let us break this example into smaller fragments and describe each part.


<ol>
<li>An instance of type <code>String</code> is created and saved into the variable named <code>s</code>.

<pre>
    final String s = "Immutable String";
    PrintUtils.print(s);
</pre>

This variable is then printed using a utilities class created for this article.  The <code>print()</code> method prints the object identity hash code (<a href="http://docs.oracle.com/javase/7/docs/api/java/lang/System.html#identityHashCode(java.lang.Object)" target="_blank">Java Doc</a>), which is 32 bit integer value, followed by the <code>String</code> value.  The <code>String</code> value is enclosed between the greater than and lower than brackets to mark the start and end of the <code>String</code> value.  This was done so that any leading or trailing spaces do not go unnoticed.  

Following is an example of such output.

<pre>
(Object: 366712642) &gt;Immutable String&lt;
</pre>

Please note that the numeric value representing the object's hash code may change between runs.
</li>


<li>
Next, we retrieved the class object of the <code>String</code> class.  This object is of type <code>Class</code>, which supports generics.
<pre>
    final Class&lt;String&gt; type = String.class;
</pre>

From the object's class, we can then obtain the methods and fields found in this class as shown next.

<pre>
    final Field valueField = type.getDeclaredField("value");
</pre>

Here we accessed the field by its name.  The method <code>getDeclaredField()</code> will either return the <code>Field</code> with the given name or throws a <code>NoSuchFieldException</code> (<a href="http://docs.oracle.com/javase/7/docs/api/java/lang/NoSuchFieldException.html" target="_blank">Java Doc</a>) if such field does not exists.  Please note that this method is case sensitive and thus the fields name must match including the case.


Finally we set the field's accessibility to <code>true</code> so that we can access the field.  Note that the <code>String</code>'s field <code>value</code> has the access modifier set to <code>private</code>, therefore we cannot access it from another class.  By setting the field's accessibility to <code>true</code>, we can access and modify its value using reflection.

<pre>
    valueField.setAccessible(true);
</pre>

Note that the above code only affects the current instance of the field object and not all instance of the field objects for the same field.  That is, by setting this value to <code>true</code> we are only modifying the current field instance and not all instance of this field.
</li>

<li>
Once we have access to the field object we can get the value it holds (or points to).

<pre>
    final char[] value = (char[]) valueField.get(s);
</pre>

In the above fragment we are retrieved in the <code>String</code>'s value character array for the <code>String</code> object created before.  Our array <code>value</code> points to the same object in the heap as the <code>String</code>'s field.  In other words, the <code>String</code>'s field value has escaped through reflection and we can modify it outside of the <code>String</code> object as shown next

<pre>
    value[0] = 'i';
    value[10] = 's';
    PrintUtils.print(s);
</pre>


In the above fragment we are changing the uppercase letters to lowercase and print the value.


<pre>
(Object: 366712642) &gt;immutable string&lt;
</pre>


Note that the printed <code>String</code> is all lowercase while the object identity hashcode is still the same.  Here we managed to modify the <code>String</code> value without creating a new <code>String</code> object.
</li>

<li>
Our experiment goes further.  Next we try to change the <code>String</code>'s content to <code>"Mutable String"</code> as shown next.

<pre>
    System.arraycopy("Mutable String".toCharArray(), 0, value, 0, 14);
    PrintUtils.print(s);
</pre>

Note that the new string is a bit shorter and thus it does not completely replace the previous value as shown next.

<pre>
(Object: 366712642) &gt;Mutable String<span class="highlight">ng</span>&lt;
</pre>

Furthermore, this approach of changing the String's value is a bit dangerous as if the new length is larger than the current <code>String</code> length, an exception of type <code>ArrayIndexOutOfBoundsException</code> (<a href="http://docs.oracle.com/javase/7/docs/api/java/lang/ArrayIndexOutOfBoundsException.html" target="_blank">Java Doc</a>) will be thrown as shown next.

<pre>
Exception in thread "main" java.lang.ArrayIndexOutOfBoundsException
	at java.lang.System.arraycopy(Native Method)
	at com.javacreed.examples.lang.Example1.main(Example1.java:19)
</pre>

We discourage such approach and prefer the following one for completely modifying the <code>String</code>'s value.
</li>

<li>
Similar to the <code>get()</code> method, the <code>Field</code> class has the <code>set()</code> method which replaces the value of this field for the given object to the new value.

<pre>
    valueField.set(s, "Mutable String".toCharArray());
    PrintUtils.print(s);
</pre>

Here we are replacing the value's object (array of characters) with a totally new object.  Notice that now, the <code>String</code> object only contains the values we want.

<pre>
(Object: 366712642) &gt;Mutable String&lt;
</pre>
</li>

</ol>


As shown and described in the above example, we can modify the <code>String</code>'s value without having to create a new <code>String</code> object.  This is quite an interesting concept which applies to any object and not just strings.  <strong>With that said, this approach breaks any object oriented principle especially encapsulation as we are able to access fields despite these are set to <code>private</code>.  Furthermore this has security implications as we can access and modify sensitive information bypassing any validations applied through methods</strong>.  Please do not take this as a language weakness as Java provides fine grain security mechanisms which prevents this as we will see in the next section.


For completeness, please find following the <code>PrintUtils</code> class.


<pre>
package com.javacreed.examples.lang;

public class PrintUtils {

  private PrintUtils() {}

  public static void print(String s) {
    System.out.printf("(Object: %d) &gt;%s&lt;%n", System.identityHashCode(s), s);
  }
}
</pre>


<h2>Java Security Manager</h2>


Despite from any negative impressions that one may have formed when reading the previous section, Java is a very secure language.  The code that we saw before will fail if a <code>SecurityManager</code> (<a href="http://docs.oracle.com/javase/7/docs/api/java/lang/SecurityManager.html" target="_blank">Java Doc</a> and <a href="http://docs.oracle.com/javase/tutorial/essential/environment/security.html" target="_blank">Tutorial</a>) is used.  


<blockquote cite="http://docs.oracle.com/javase/tutorial/essential/environment/security.html">
A security manager is an object that defines a security policy for an application. This policy specifies actions that are unsafe or sensitive. Any actions not allowed by the security policy cause a <code>SecurityException</code> to be thrown. An application can also query its security manager to discover which actions are allowed.
</blockquote>


Using a security manager we can control what methods can be invoked and fail with a <code>SecurityException</code> (<a href="http://docs.oracle.com/javase/7/docs/api/java/lang/SecurityException.html" target="_blank">Java Doc</a>) if such methods are invoked in a manner which is not permitted by our security policy.  The following example shows how to enable the security manager for our example.

<pre>
package com.javacreed.examples.lang;

import java.lang.reflect.Field;

public class Example2 {
  public static void main(final String[] args) throws Exception {
    <span class="comments">// java -Djava.security.manager</span>
    <span class="highlight">System.setSecurityManager(new SecurityManager());</span>

    final Class&lt;String&gt; type = String.class;
    <span class="comments">// The following line fails with an AccessControlException exception</span>
    final Field valueField = type.getDeclaredField("value");
    valueField.setAccessible(true);
  }
}
</pre>


After setting a security manager, the above code will fail with an <code>AccessControlException</code> (<a href="http://docs.oracle.com/javase/7/docs/api/java/security/AccessControlException.html" target="_blank">Java Doc</a>) exception as shown next.

<pre>
Exception in thread "main" java.security.AccessControlException: access denied ("java.lang.RuntimePermission" "accessDeclaredMembers")
	at java.security.AccessControlContext.checkPermission(AccessControlContext.java:457)
	at java.security.AccessController.checkPermission(AccessController.java:884)
	at java.lang.SecurityManager.checkPermission(SecurityManager.java:549)
	at java.lang.Class.checkMemberAccess(Class.java:2335)
	at java.lang.Class.getDeclaredField(Class.java:2054)
	at com.javacreed.examples.lang.Example2.main(Example2.java:12)
</pre>


The default security manager does not allow us to access the <code>String</code>'s field <code>value</code> and thus protects the rest of the system from malicious code.  What if we need to make an exception?  In the next section we will see how to use a security manager and still allows access to the <code>String</code>'s field value.


The security manager can be defined as a command line argument as shown in the following example.


<pre>
java -Djava.security.manager 
</pre>


Furthermore, the security manager can also prevent that the it is replaced at runtime.  In other words, it protects against code that tries to disable it.


<h2>Making Exceptions</h2>


The security manager can be customised to provide very fine grain security checks.  We will not go through any depth about what the security manager can do in this article.  On the contrary, we will simply skim the very surface and show how the security managed can be extended to allow access to the <code>String</code>'s <code>value</code> field.


<pre>
package com.javacreed.examples.lang;

import java.lang.reflect.Field;
import java.security.Permission;
import java.util.Arrays;

public class Example3 {
  public static void main(final String[] args) throws Exception {
    System.setSecurityManager(new SecurityManager() {
      @Override
      public void checkPermission(final Permission perm) {
        switch (perm.getName()) {
        <span class="comments">// Allows getDeclaredField()</span>
        case "accessDeclaredMembers":
        <span class="comments">// Allows setAccessible()</span>
        case "suppressAccessChecks":
        <span class="comments">// Allows formatting and printing</span>
        case "user.language.format":
        case "user.script.format":
        case "user.country.format":
        case "user.variant.format":
        case "java.locale.providers":
          <span class="comments">// Ignore/Allow</span>
          break;
        default:
          super.checkPermission(perm);
        }
      }
    });

    final Class&lt;String&gt; type = String.class;
    final Field valueField = type.getDeclaredField("value");
    valueField.setAccessible(true);

    final String s = "Immutable String";
    PrintUtils.print(s);
    final char[] value = (char[]) valueField.get(s);
    value[0] = 'i';
    value[10] = 's';
    PrintUtils.print(s);

    Arrays.fill(value, (char) 0);
    System.arraycopy("Mutable String".toCharArray(), 0, value, 0, 14);
    PrintUtils.print(s);

    valueField.set(s, "Mutable String".toCharArray());
    PrintUtils.print(s);
  }
}
</pre>


In the above example we extended the <code>SecurityManager</code> on-the-fly and created an inner anonymous class (<a href="http://docs.oracle.com/javase/tutorial/java/javaOO/anonymousclasses.html" target="_blank">Tutorial</a>) which overrided the method <code>checkPermission()</code>, shown next.

<pre>
    System.setSecurityManager(new SecurityManager() {
      @Override
      public void checkPermission(final Permission perm) {
        switch (perm.getName()) {
        <span class="comments">// Allows getDeclaredField()</span>
        case "accessDeclaredMembers":
        <span class="comments">// Allows setAccessible()</span>
        case "suppressAccessChecks":
        <span class="comments">// Allows formatting and printing</span>
        case "user.language.format":
        case "user.script.format":
        case "user.country.format":
        case "user.variant.format":
        case "java.locale.providers":
          <span class="comments">// Ignore/Allow</span>
          break;
        default:
          super.checkPermission(perm);
        }
      }
    });
</pre>


In this trivial example, we are accepting anything which action name (defined by <code>Permission.getName()</code> method, <a href="http://docs.oracle.com/javase/7/docs/api/java/security/Permission.html#getName()" target="_blank">Java Doc</a>) matches the list provided above.  Any other checks, are delegated to the original security manager.


As one can see from the number of exception that we had to add, through the security manager we can control almost anything and fail if something is not as we expect it to be.  The security manager provides a great deal of fine grained security and provides a sound fence for untrusted code.


<h2>Collateral Damage</h2>


While the topic discussed here may seem attractive, it has some collateral damage which may produce undesired results.  Java makes use of string pool and all string literals always refer to the same instance as described in the Java Language Specification (<a href="http://docs.oracle.com/javase/specs/jls/se7/html/jls-3.html#jls-3.10.5" target="_blank">reference</a>).


<blockquote cite="http://docs.oracle.com/javase/specs/jls/se7/html/jls-3.html#jls-3.10.5">
Moreover, a string literal always refers to the same instance of class <code>String</code>. This is because string literals - or, more generally, strings that are the values of constant expressions (<a href="http://docs.oracle.com/javase/specs/jls/se7/html/jls-15.html#jls-15.28" target="_blank">§15.28</a>) - are "<em>interned</em>" so as to share unique instances, using the method <code>String.intern</code>.
</blockquote>


Therefore, modifying one <code>String</code> literal will affect all literals of the same string.  Consider the following example.


<pre>
package com.javacreed.examples.lang;

import java.lang.reflect.Field;

public class Example4 {

  public static void main(final String[] args) throws Exception {
    final Class&lt;String&gt; type = String.class;
    final Field valueField = type.getDeclaredField("value");
    valueField.setAccessible(true);

    final String s = "Immutable String";
    valueField.set(s, "Mutable String".toCharArray());

    System.out.println("Immutable String");

    final String o = "Immutable String";
    System.out.println(o);
  }
}
</pre>


What will the above code print?  Unfortunately it prints the modified string, despite we are printing the string literal as shown next.


<pre>
Mutable String
Mutable String
</pre>


This change affects all code that is running on the same JVM.  While one may like the idea of modifying <code>String</code>s without creating a new instance, this is highly discouraged.  Java provides specific classes that support mutable strings and these should be used instead of this approach.


<h2>Conclusion</h2>


The Java API provides two variants of mutable string, <code>StringBuilder</code> (<a href="http://docs.oracle.com/javase/7/docs/api/java/lang/StringBuilder.html" target="_blank">Java Doc</a>) and <code>StringBuffer</code> (<a href="http://docs.oracle.com/javase/7/docs/api/java/lang/StringBuffer.html" target="_blank">Java Doc</a>).  These classes can be safely modified without breaking any object oriented principles.  The <code>StringBuilder</code> is not thread safe and should be preferred when thread safety is not a concern.  The <code>StringBuffer</code> provides thread-safety but it is a bit slower than the <code>StringBuilder</code>.  Changing something that should not be changed may be fun, but can have far reaching effects.  Such changes are propagated throughout the JVM since string literals are cached and reused.  Security managers should be used with sensitive applications to make sure that developers do not, intentionally or unintentionally, execute code which should not be executed.
