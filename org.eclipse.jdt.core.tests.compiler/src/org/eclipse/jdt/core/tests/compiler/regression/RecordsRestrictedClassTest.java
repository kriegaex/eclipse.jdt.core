/*******************************************************************************
 * Copyright (c) 2019 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * This is an implementation of an early-draft specification developed under the Java
 * Community Process (JCP) and is made available for testing and evaluation purposes
 * only. The code is not compatible with any specification of the JCP.
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.core.tests.compiler.regression;

import java.util.Map;

import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;

import junit.framework.Test;

public class RecordsRestrictedClassTest extends AbstractRegressionTest {

	static {
//		TESTS_NUMBERS = new int [] { 40 };
//		TESTS_RANGE = new int[] { 1, -1 };
//		TESTS_NAMES = new String[] { "testBug550750_019" };
	}
	
	public static Class<?> testClass() {
		return RecordsRestrictedClassTest.class;
	}
	public static Test suite() {
		return buildMinimalComplianceTestSuite(testClass(), F_14);
	}
	public RecordsRestrictedClassTest(String testName){
		super(testName);
	}

	// Enables the tests to run individually
	protected Map<String, String> getCompilerOptions() {
		Map<String, String> defaultOptions = super.getCompilerOptions();
		defaultOptions.put(CompilerOptions.OPTION_Compliance, CompilerOptions.VERSION_14); // FIXME
		defaultOptions.put(CompilerOptions.OPTION_Source, CompilerOptions.VERSION_14);
		defaultOptions.put(CompilerOptions.OPTION_TargetPlatform, CompilerOptions.VERSION_14);
		defaultOptions.put(CompilerOptions.OPTION_EnablePreviews, CompilerOptions.ENABLED);
		defaultOptions.put(CompilerOptions.OPTION_ReportPreviewFeatures, CompilerOptions.IGNORE);
		return defaultOptions;
	}
	
	@Override
	protected void runConformTest(String[] testFiles, String expectedOutput) {
		runConformTest(testFiles, expectedOutput, getCompilerOptions());
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected void runConformTest(String[] testFiles, String expectedOutput, Map customOptions) {
		Runner runner = new Runner();
		runner.testFiles = testFiles;
		runner.expectedOutputString = expectedOutput;
		runner.vmArguments = new String[] {"--enable-preview"};
		runner.customOptions = customOptions;
		runner.javacTestOptions = JavacTestOptions.forReleaseWithPreview("14");
		runner.runConformTest();
	}
	@Override
	protected void runNegativeTest(String[] testFiles, String expectedCompilerLog) {
		runNegativeTest(testFiles, expectedCompilerLog, JavacTestOptions.forReleaseWithPreview("14"));
	}
	protected void runWarningTest(String[] testFiles, String expectedCompilerLog) {
		runWarningTest(testFiles, expectedCompilerLog, null);
	}
	protected void runWarningTest(String[] testFiles, String expectedCompilerLog, Map<String, String> customOptions) {
		runWarningTest(testFiles, expectedCompilerLog, customOptions, null);
	}
	protected void runWarningTest(String[] testFiles, String expectedCompilerLog,
			Map<String, String> customOptions, String javacAdditionalTestOptions) {

		Runner runner = new Runner();
		runner.testFiles = testFiles;
		runner.expectedCompilerLog = expectedCompilerLog;
		runner.customOptions = customOptions;
		runner.vmArguments = new String[] {"--enable-preview"};
		runner.javacTestOptions = javacAdditionalTestOptions == null ? JavacTestOptions.forReleaseWithPreview("14") :
			JavacTestOptions.forReleaseWithPreview("14", javacAdditionalTestOptions);
		runner.runWarningTest();
	}
	public void testBug550750_001() {
		runConformTest(
				new String[] {
						"X.java",
						"class X {\n"+
						"  public static void main(String[] args){\n"+
						"     System.out.println(0);\n" +
						"  }\n"+
						"}\n"+
						"record Point(int x, int y){\n"+
						"}\n"
				},
			"0");
	}
	public void testBug550750_002() {
		this.runNegativeTest(
			new String[] {
				"X.java",
				"class X {\n"+
				"  public static void main(String[] args){\n"+
				"     System.out.println(0);\n" +
				"  }\n"+
				"}\n"+
				"abstract record Point(int x, int y){\n"+
			"}",
			},
			"----------\n" + 
			"1. ERROR in X.java (at line 6)\n" + 
			"	abstract record Point(int x, int y){\n" + 
			"	                ^^^^^^^^^^^^^^^^^^^\n" + 
			"Illegal modifier for the record Point; abstract not allowed\n" + 
			"----------\n");
	}
	/* A record declaration is implicitly final. It is permitted for the declaration of
	 * a record type to redundantly specify the final modifier. */
	public void testBug550750_003() {
		runConformTest(
				new String[] {
						"X.java",
						"class X {\n"+
						"  public static void main(String[] args){\n"+
						"     System.out.println(0);\n" +
						"  }\n"+
						"}\n"+
						"final record Point(int x, int y){\n"+
						"}\n"
				},
			"0");
	}
	public void testBug550750_004() {
		this.runNegativeTest(
			new String[] {
				"X.java",
				"class X {\n"+
				"  public static void main(String[] args){\n"+
				"     System.out.println(0);\n" +
				"  }\n"+
				"}\n"+
				"final final record Point(int x, int y){\n"+
			"}",
			},
			"----------\n" + 
			"1. ERROR in X.java (at line 6)\n" + 
			"	final final record Point(int x, int y){\n" + 
			"	                   ^^^^^^^^^^^^^^^^^^^\n" + 
			"Duplicate modifier for the type Point\n" + 
			"----------\n");
	}
	public void testBug550750_005() {
		runConformTest(
				new String[] {
						"X.java",
						"class X {\n"+
						"  public static void main(String[] args){\n"+
						"     System.out.println(0);\n" +
						"  }\n"+
						"}\n"+
						"final record Point(int x, int y){\n"+
						"}\n"
				},
			"0");
	}
	public void testBug550750_006() {
		this.runNegativeTest(
			new String[] {
				"X.java",
				"public public record X(int x, int y){\n"+
			"}",
			},
			"----------\n" + 
			"1. ERROR in X.java (at line 1)\n" + 
			"	public public record X(int x, int y){\n" + 
			"	                     ^^^^^^^^^^^^^^^\n" + 
			"Duplicate modifier for the type X\n" + 
			"----------\n");
	}
	public void testBug550750_007() {
		runConformTest(
				new String[] {
						"X.java",
						"final record Point(int x, int y){\n"+
						"  public void foo() {}\n"+
						"}\n"+
						"public class X {\n"+
						"  public static void main(String[] args){\n"+
						"     System.out.println(0);\n" +
						"  }\n"+
						"}\n"
				},
			"0");
	}
	public void testBug550750_008() {
		runConformTest(
				new String[] {
						"X.java",
						"final record Point(int x, int y){\n"+
						"  public Point {}\n"+
						"}\n"+
						"public class X {\n"+
						"  public static void main(String[] args){\n"+
						"     System.out.println(0);\n" +
						"  }\n"+
						"}\n"
				},
			"0");
	}
	public void testBug550750_009() {
		runConformTest(
				new String[] {
						"X.java",
						"final record Point(int x, int y){\n"+
						"  public Point {}\n"+
						"  public void foo() {}\n"+
						"}\n"+
						"public class X {\n"+
						"  public static void main(String[] args){\n"+
						"     System.out.println(0);\n" +
						"  }\n"+
						"}"
				},
			"0");
	}
	 /* nested record implicitly static*/
	public void testBug550750_010() {
		runConformTest(
				new String[] {
						"X.java",
						"class X {\n"+
						"  public static void main(String[] args){\n"+
						"     System.out.println(0);\n" +
						"  }\n"+
						"  record Point(int x, int y){\n"+
						"  }\n"+
						"}\n"
				},
			"0");
	}
	 /* nested record explicitly static*/
	public void testBug550750_011() {
		runConformTest(
				new String[] {
						"X.java",
						"class X {\n"+
						"  public static void main(String[] args){\n"+
						"     System.out.println(0);\n" +
						"  }\n"+
						"  static record Point(int x, int y){\n"+
						"  }\n"+
						"}\n"
				},
			"0");
	}
	public void testBug550750_012() {
		runConformTest(
				new String[] {
						"X.java",
						"class X {\n"+
						"  public static void main(String[] args){\n"+
						"     System.out.println(0);\n" +
						"  }\n"+
						"}\n"+
						"record Point(int ... x){\n"+
						"}\n"
				},
			"0");
	}
	public void testBug550750_013() {
		runConformTest(
				new String[] {
						"X.java",
						"import java.lang.annotation.Target;\n"+
						"import java.lang.annotation.ElementType;\n"+
						"class X {\n"+
						"  public static void main(String[] args){\n"+
						"     System.out.println(0);\n" +
						"  }\n"+
						"}\n"+
						"record Point(int myInt, char myChar) {}\n"+
						" @Target({ElementType.FIELD, ElementType.TYPE})\n"+
						" @interface MyAnnotation {}\n"
				},
			"0");
	}
	public void testBug550750_014() {
		runConformTest(
				new String[] {
						"X.java",
						"public class X {\n"+
						"  public static void main(String[] args){\n"+
						"     System.out.println(0);\n" +
						"  }\n"+
						"}\n"+
						"record Point(int myInt, char myChar) {\n"+
						"  public int myInt(){\n"+
						"     return this.myInt;\n" +
						"  }\n"+
						"}\n"
				},
			"0");
	}
	public void testBug550750_015() {
		runConformTest(
				new String[] {
						"X.java",
						"public class X {\n"+
						"  public static void main(String[] args){\n"+
						"     System.out.println(0);\n" +
						"  }\n"+
						"}\n"+
						"record Point(int myInt, char myChar) I {\n"+
						"  public int myInt(){\n"+
						"     return this.myInt;\n" +
						"  }\n"+
						"}\n" +
						"interface I {}\n"
				},
			"0");
	}
	public void testBug550750_016() {
		runConformTest(
				new String[] {
						"X.java",
						"public class X {\n"+
						"  public static void main(String[] args){\n"+
						"     System.out.println(0);\n" +
						"  }\n"+
						"}\n"+
						"record Point(int myInt, char myChar) I {\n"+
						"}\n" +
						"interface I {}\n"
				},
			"0");
	}
	public void testBug550750_017() {
		runConformTest(
				new String[] {
						"X.java",
						"public class X {\n"+
						"  public static void main(String[] args){\n"+
						"     System.out.println(0);\n" +
						"  }\n"+
						"}\n"+
						"record Point(int myInt, char myChar) I {\n"+
						"  public Point(int myInt, char myChar){\n"+
						"     this.myInt = myInt;\n" +
						"     this.myChar = myChar;\n" +
						"  }\n"+
						"}\n" +
						"interface I {}\n"
				},
			"0");
	}
	public void testBug550750_018() {
		this.runNegativeTest(
				new String[] {
						"X.java",
						"public class X {\n"+
						"  public static void main(String[] args){\n"+
						"     System.out.println(0);\n" +
						"  }\n"+
						"}\n"+
						"record Point(int myInt, char myChar) I {\n"+
						"  public Point(int myInt, char myChar){\n"+
						"  }\n"+
						"}\n" +
						"interface I {}\n"
				},
			"----------\n" + 
			"1. ERROR in X.java (at line 7)\n" + 
			"	public Point(int myInt, char myChar){\n" + 
			"	       ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^\n" + 
			"The blank final field myChar may not have been initialized\n" + 
			"----------\n" + 
			"2. ERROR in X.java (at line 7)\n" + 
			"	public Point(int myInt, char myChar){\n" + 
			"	       ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^\n" + 
			"The blank final field myInt may not have been initialized\n" + 
			"----------\n");
	}
	public void testBug550750_019() {
		this.runNegativeTest(
				new String[] {
						"X.java",
						"public class X {\n"+
						"  public static void main(String[] args){\n"+
						"     System.out.println(0);\n" +
						"  }\n"+
						"}\n"+
						"record Point(int myInt, char myChar) I {\n"+
						"  private Point {\n"+
						"     this.myInt = myInt;\n" +
						"     this.myChar = myChar;\n" +
						"  }\n"+
						"}\n" +
						"interface I {}\n"
				},
			"----------\n" + 
			"1. ERROR in X.java (at line 7)\n" + 
			"	private Point {\n" + 
			"	        ^^\n" + 
			"The compact constructor Point must be declared public.\n" + 
			"----------\n");
	}
	public void testBug550750_020() {
		this.runNegativeTest(
				new String[] {
						"X.java",
						"public class X {\n"+
						"  public static void main(String[] args){\n"+
						"     System.out.println(0);\n" +
						"  }\n"+
						"}\n"+
						"record Point(int myInt, char myChar) I {\n"+
						"  protected Point {\n"+
						"     this.myInt = myInt;\n" +
						"     this.myChar = myChar;\n" +
						"  }\n"+
						"}\n" +
						"interface I {}\n"
				},
			"----------\n" + 
			"1. ERROR in X.java (at line 7)\n" + 
			"	protected Point {\n" + 
			"	          ^^\n" + 
			"The compact constructor Point must be declared public.\n" + 
			"----------\n");
	}
	public void testBug550750_021() {
		runConformTest(
				new String[] {
						"X.java",
						"public class X {\n"+
						"  public static void main(String[] args){\n"+
						"     System.out.println(0);\n" +
						"  }\n"+
						"}\n"+
						"record Point(int myInt, char myChar) I {\n"+
						"  public Point {\n"+
						"     this.myInt = myInt;\n" +
						"     this.myChar = myChar;\n" +
						"  }\n"+
						"}\n" +
						"interface I {}\n"
				},
			"0");
	}
	public void testBug550750_022() {
		this.runNegativeTest(
				new String[] {
						"X.java",
						"public class X {\n"+
						"  public static void main(String[] args){\n"+
						"     System.out.println(0);\n" +
						"  }\n"+
						"}\n"+
						"record Point(int myInt, char myChar) I {\n"+
						"  public Point {\n"+
						"     this.myInt = myInt;\n" +
						"     this.myChar = myChar;\n" +
						"     return;\n" +
						"  }\n"+
						"}\n" +
						"interface I {}\n"
				},
			"----------\n" + 
			"1. ERROR in X.java (at line 10)\n" + 
			"	return;\n" + 
			"	^^^^^^^\n" + 
			"The body of a compact constructor must not contain a return statement\n" + 
			"----------\n");
	}
	public void testBug550750_023() {
		this.runNegativeTest(
				new String[] {
						"X.java",
						"public class X {\n"+
						"  public static void main(String[] args){\n"+
						"     System.out.println(0);\n" +
						"  }\n"+
						"}\n"+
						"record Point(int myInt, int finalize) I {\n"+
						"  public Point {\n"+
						"     this.myInt = myInt;\n" +
						"  }\n"+
						"}\n" +
						"interface I {}\n"
				},
			"----------\n" + 
			"1. ERROR in X.java (at line 6)\n" + 
			"	record Point(int myInt, int finalize) I {\n" + 
			"	                            ^^^^^^^^\n" + 
			"Illegal component name finalize in record Point;\n" + 
			"----------\n");
	}
	public void testBug550750_024() {
		this.runNegativeTest(
				new String[] {
						"X.java",
						"public class X {\n"+
						"  public static void main(String[] args){\n"+
						"     System.out.println(0);\n" +
						"  }\n"+
						"}\n"+
						"record Point(int myInt, int finalize, int myZ) I {\n"+
						"  public Point {\n"+
						"     this.myInt = myInt;\n" +
						"     this.myZ = myZ;\n" +
						"  }\n"+
						"}\n" +
						"interface I {}\n"
				},
			"----------\n" + 
			"1. ERROR in X.java (at line 6)\n" + 
			"	record Point(int myInt, int finalize, int myZ) I {\n" + 
			"	                            ^^^^^^^^\n" + 
			"Illegal component name finalize in record Point;\n" + 
			"----------\n");
	}
	public void testBug550750_025() {
		this.runNegativeTest(
				new String[] {
						"X.java",
						"public class X {\n"+
						"  public static void main(String[] args){\n"+
						"     System.out.println(0);\n" +
						"  }\n"+
						"}\n"+
						"record Point(int myInt, int myZ, int myZ) I {\n"+
						"  public Point {\n"+
						"     this.myInt = myInt;\n" +
						"     this.myZ = myZ;\n" +
						"  }\n"+
						"}\n" +
						"interface I {}\n"
				},
			"----------\n" + 
			"1. ERROR in X.java (at line 6)\n" + 
			"	record Point(int myInt, int myZ, int myZ) I {\n" + 
			"	                                     ^^^\n" + 
			"Duplicate component myZ in record\n" + 
			"----------\n");
	}
	public void testBug550750_026() {
		this.runNegativeTest(
				new String[] {
						"X.java",
						"public class X {\n"+
						"  public static void main(String[] args){\n"+
						"     System.out.println(0);\n" +
						"  }\n"+
						"}\n"+
						"record Point(int myInt, int myInt, int myInt, int myZ) I {\n"+
						"  public Point {\n"+
						"     this.myInt = myInt;\n" +
						"     this.myZ = myZ;\n" +
						"  }\n"+
						"}\n" +
						"interface I {}\n"
				},
			"----------\n" + 
			"1. ERROR in X.java (at line 6)\n" + 
			"	record Point(int myInt, int myInt, int myInt, int myZ) I {\n" + 
			"	                            ^^^^^\n" + 
			"Duplicate component myInt in record\n" + 
			"----------\n" + 
			"2. ERROR in X.java (at line 6)\n" + 
			"	record Point(int myInt, int myInt, int myInt, int myZ) I {\n" + 
			"	                                       ^^^^^\n" + 
			"Duplicate component myInt in record\n" + 
			"----------\n");
	}
	public void testBug550750_027() {
		this.runNegativeTest(
				new String[] {
						"X.java",
						"public class X {\n"+
						"  public static void main(String[] args){\n"+
						"     System.out.println(0);\n" +
						"  }\n"+
						"}\n"+
						"record Point(int myInt, int myZ) I {\n"+
						"  static final int z;\n"+
						"  public Point {\n"+
						"     this.myInt = myInt;\n" +
						"     this.myZ = myZ;\n" +
						"  }\n"+
						"}\n" +
						"interface I {}\n"
				},
			"----------\n" + 
			"1. ERROR in X.java (at line 7)\n" + 
			"	static final int z;\n" + 
			"	                 ^\n" + 
			"The blank final field z may not have been initialized\n" + 
			"----------\n");
	}
	public void testBug550750_028() {
		this.runNegativeTest(
				new String[] {
						"X.java",
						"public class X {\n"+
						"  public static void main(String[] args){\n"+
						"     System.out.println(0);\n" +
						"  }\n"+
						"}\n"+
						"record Point(int myInt, int myZ) I {\n"+
						"  int z;\n"+
						"  public Point {\n"+
						"     this.myInt = myInt;\n" +
						"     this.myZ = myZ;\n" +
						"  }\n"+
						"}\n" +
						"interface I {}\n"
				},
			"----------\n" + 
			"1. ERROR in X.java (at line 7)\n" + 
			"	int z;\n" + 
			"	    ^\n" + 
			"User declared non-static fields z are not permitted in a record\n" + 
			"----------\n");
	}
	public void testBug550750_029() {
		this.runNegativeTest(
				new String[] {
						"X.java",
						"public class X {\n"+
						"  public static void main(String[] args){\n"+
						"     System.out.println(0);\n" +
						"  }\n"+
						"}\n"+
						"record Point(int myInt, int myZ) I {\n"+
						"  public Point {\n"+
						"     this.myInt = myInt;\n" +
						"     this.myZ = myZ;\n" +
						"  }\n"+
						"  public native void foo();\n"+
						"}\n" +
						"interface I {}\n"
				},
			"----------\n" + 
			"1. ERROR in X.java (at line 11)\n" + 
			"	public native void foo();\n" + 
			"	                   ^^^^^\n" + 
			"Illegal modifier native for method foo; native methods are not allowed in record\n" + 
			"----------\n");
	}
	public void testBug550750_030() {
		this.runNegativeTest(
				new String[] {
						"X.java",
						"public class X {\n"+
						"  public static void main(String[] args){\n"+
						"     System.out.println(0);\n" +
						"  }\n"+
						"}\n"+
						"record Point(int myInt, int myZ) I {\n"+
						"  {\n"+
						"     System.out.println(0);\n" +
						"  }\n"+
						"}\n" +
						"interface I {}\n"
				},
			"----------\n" + 
			"1. ERROR in X.java (at line 7)\n" + 
			"	{\n" + 
			"     System.out.println(0);\n" + 
			"  }\n" + 
			"	^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^\n" + 
			"Instance Initializer is not allowed in a record declaration \n" + 
			"----------\n");
	}
	public void testBug550750_031() {
		runConformTest(
				new String[] {
						"X.java",
						"public class X {\n"+
						"  public static void main(String[] args){\n"+
						"     System.out.println(0);\n" +
						"  }\n"+
						"}\n"+
						"record Point(int myInt, int myZ) I {\n"+
						"  static {\n"+
						"     System.out.println(0);\n" +
						"  }\n"+
						"}\n" +
						"interface I {}\n"
				},
			"0");
	}
}