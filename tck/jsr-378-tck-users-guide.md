# JSR 378 User's Guide

---

## Copyright/License Notice

Portlet 3.0 Bridge for JavaServer™ Faces 2.2 (FacesBridge 5.x)

Compatibility Kit User’s Guide 

For Technology Licensees 

Version 5.0.0, May 2022

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
language governing permissions and limitations under the License.

---

## Preface

This guide describes how to install, configure, and run the Technology Compatibility Kit (TCK) that provides tests for
the FacesBridge 5.x (FacesBridge TCK). The FacesBridge TCK is designed as a portable, configurable automated test suite
for verifying the compliance of a licensee’s implementation of the FacesBridge 5.x Specification (hereafter referred to
as a licensee implementation). It may also be used to verify that any compatible version of the bridge including the
Reference Implementation runs correctly in a given Java EE, Portlet container and Faces environment. The FacesBridge TCK runs
as a Maven project located at GitHub:

https://github.com/liferay/liferay-faces-bridge-impl/tree/5.x/tck

---

## Who Should Use This Book

This guide is for licensees of Liferay, Inc. FacesBridge 5.x technology to assist them in running the test suite that
verifies compliance of their implementation of the FacesBridge 5.x Specification.

NOTE All references to specific Web URLs are given for your convenience in locating the resources quickly. These
references are always subject to changes that are in many cases beyond the control of the authors of this guide.

---

## Before You Read This Book

Before reading this guide, you should familiarize yourself with the Java programming language, Portlet 3.0 standard (JSR
362), JavaServer Faces 2.2 standard (JSR 344) and the FacesBridge 5.x Specification (JSR 378). The FacesBridge TCK
is based on the JSR 378 Specification. Links to the specification and other product information can be found on the Web
at: http://www.jcp.org/en/jsr/detail?id=378

---

## Introduction 
 
This chapter provides an overview of the principles that apply generally to all Technology Compatibility Kits (TCKs) and
describes the FacesBridge 5.x Compatibility Kit (FacesBridge TCK). It also includes a listing of what is needed to get
up and running with the FacesBridge TCK.

### Compatibility Testing

Compatibility testing differs from traditional product testing in a number of ways. The focus of compatibility testing
is to test those features and areas of an implementation that are likely to differ across implementations. It attempts
to ensure that users of any given implementation will receive the same results regardless of the underlying
implementation strategy. Compatibility test development for a given feature relies on a complete specification and
reference implementation for that feature. Compatibility testing is not primarily concerned with robustness,
performance, or ease of use.

### Why Compatibility Testing is Important

Java™ platform compatibility is important to different groups involved with Java technologies for different reasons:

-  Compatibility testing is the means by which Oracle Corporation ensures that the Java platform does not become
   fragmented as it is ported to different operating systems and hardware environments.
-  Compatibility testing benefits developers working in the Java programming language, allowing them to write
   applications once and then to deploy them across heterogeneous computing environments without porting.
-  Compatibility testing allows application users to obtain applications from disparate sources and deploy them with
   confidence.
-  Compatibility testing benefits Java platform implementors by ensuring a level playing field for all Java platform
   ports.

### TCK Compatibility Rules

Compatibility criteria for all technology implementations are embodied in the TCK Compatibility Rules that apply to a
specified technology. Each TCK tests for adherence to these Rules as described in the section titled "Procedure for
FacesBridge 5.x Certification."

### TCK Overview

A TCK is a set of tools and tests used to verify that a licensee’s implementation of Liferay Inc.'s FacesBridge 5.x
technology conforms to the applicable specification. All tests in the TCK are based on the written specifications for
the Java platform. Compatibility testing is a means of ensuring correctness, completeness, and consistency across all
implementations. The set of tests included with each TCK is called the "test suite." All tests in the FacesBridge TCK
test suite are self-checking and do not require tester interaction. Most tests return either a Pass or Fail status. For
a given licensee’s implementation to be certified, all of the required tests must pass. The definition of required tests
will change over time. Before your final certification test pass, be sure to download the latest Excludes File for the
TCK you are using from the TCK site.

e.g.:

- [pluto-tests.xml](https://github.com/liferay/liferay-faces-bridge-impl/blob/5.x/tck/bridge-tck-main-portlet/src/test/resources/pluto-tests.xml)
- [liferay-tests.xml](https://github.com/liferay/liferay-faces-bridge-impl/blob/5.x/tck/bridge-tck-main-portlet/src/test/resources/liferay-tests.xml)

### Java Community Process (JCP) Program and Compatibility Testing

The Java Community Process℠ (JCP℠) program is the formalization of the open process that Oracle Corporation (and
prior to that, Sun Microsystems, Inc.) has been using since 1995 to develop and revise Java technology specifications in
cooperation with the international Java community. The JCP program specifies that the following three major components
must be included as deliverables in a final Java technology release under the direction of the responsible Expert Group:

-  Technology Specification
-  Reference Implementation
-  Technology Compatibility Kit (TCK)

For further information on the JCP program see this URL: http://www.jcp.org.

### The FacesBridge TCK

The FacesBridge TCK is designed as a portable, configurable, automated test suite for verifying the compliance of
a licensee’s implementation with Liferay Inc.'s FacesBridge 5.x specification.

### FacesBridge TCK Specifications and Requirements

This section lists the applicable requirements and specifications. 

- Specification Requirements – Software requirements for a Bridge implementation are described in detail in the
  FacesBridge 5.x Specification. Links to the JSR 378 specification and other product information can be found at
  http://www.jcp.org/en/jsr/detail?id=378.
- Reference Implementation – The designated Reference Implementation for conformance testing of implementations based
  upon JSR 378 Specification is Liferay Faces Bridge.

### The FacesBridge TCK

The FacesBridge TCK is composed of:

-  a teststuite, which is a collection of tests and supplemental files that provide data for the automatic running of
   tests through the test harness.
-  an exclude list, which provides a list of tests that your implementation is not required to pass.
-  TCK documentation.
-  a set of maven pom.xml files providing the necessary instructions for building the test stuite for your test
   environment and running the test harness to verify your environment.

The FacesBridge TCK is a maven project maintained at GitHub as part of the Liferay Faces umblrella project. The TCK test
suite is built and run using maven commands. The test harness, used to automate executing the TCK tests uses JUnit and
Selenium. Maven automatically accesses the necessary versions and runs them during the appropriate phase of the
lifecycle. More information can be found on the web:

-  Maven: consult the Apache Maven website (http://maven.apache.org/).
-  JUnit: consult the JUnit website (http://www.junit.org/).
-  Selenium: consult the Selenium website (http://seleniumhq.org/)

### Typical Usage

Each TCK test is implemented in its own specific portlet built from the test suite source. Most test portlets are
grouped into a single portlet application. Each of these test suite portlet applications is built using a maven
command. As described in detail later, the command used to generate the war files includes flexibility for:

-  packaging a specific Faces 2.2 implementation type and version (e.g. Mojarra) into the war. This is used in
   situations where your application server isn't a fully compliant Java EE™ 7 server (i.e. one that includes Faces 2.2)
-  packaging your bridge implementation into the war. This is used if your bridge isn't already deployed for use in the
   application server as part of the portal/portlet container deployment.
-  including a proper deployment descriptor for deploying the applications in a Pluto 3.0 environment.

Once generated, the testsuite wars are deployed to the host application server. The test server(s) must not only contain
a Portlet 3.0 (compatible) container to host the portlet application but must also include a technology for accessing
and executing each portlet in these applications by URL. Typically, this involves a portal application hosting a set of
portal pages where each page contains a distinct test portlet.

Note: The test harness requires a distinct URL to execute each test. In most cases this translates into placing each
test portlet on a distinct page.

Once the test applications are deployed and made accessible by URL, the TCK is executed by running a second maven
command. This command reads a test file containing the test URLs (provided by you) and executes each test in the file
via the JUnit/Selenium harness. Upon completion a report is generated providing summary and detailed information of the
test results.

Note: Prior to running the automated test harness you are encouraged to verify your portal/test suite configuration by
invoking some of the test pages directly from a browser. These tests are designed to ouput valid HTML indicating the
test status along with the internal tags used by the harness.

### TCK Compatibility Test Suite

The test suite is the collection of tests used by the test harness to test the compliance of a given Bridge in a given
runtime environment. The tests are designed to verify that a licensee’s implementation of the technology complies with
the appropriate specification. The individual tests correspond to assertions of the specification.

The FacesBridge TCK test suite comprises two test categories:

-  A signature test that checks that the public APIs are supported in the implementation that is being tested.
-  Functional tests that check for behavior correctness for all the (testable) assertions in the specification.

### Exclude Lists

Each version of a TCK includes an Exclude List file. This file identifies tests which do not have to be run as their
results may not be repeatable in all environments. Whenever tests are run, the test harness automatically excludes any
test on the Exclude List from being executed.

A licensee is not required to pass any test—or even run any test—on the Exclude List. However, as many of these tests
are excluded because of specific environmental limitations, licensee's are encouraged to review the comments in the
excluded file to determine if the test can be successfully run in he or her environment, and if so, to include the test
to verify proper behavior.

Note: Licensees are not permitted to alter or modify the Exclude Lists. Changes to an Exclude List can only be made by
using the procedure described in "Portlet Test Appeals Process".

### FacesBridge TCK Started

This section provides a general overview of what needs to be done to install, set up, test, and use the FacesBridge TCK:

1. Make sure that the following software has been correctly installed:

-  Sun Microsystems JDK software version 8 on the system you are accessing the TCK subversion repository from and from
   which you will build the tests and run the harness.
-  The latest version of Maven (http://maven.apache.org/download.html)
-  An application server containing a portal/Java Portlet 3.0 (compatible) portlet container.

2. Install the FacesBridge TCK software.
3. Build the FacesBrige TCK portlet applications using Maven.
4. Deploy the generated applications to your application server.
5. Construct individual test pages, one for each test, using the installed portal.
6. Verify the test pages/tests by manually accessing a few pages.
7. Generate a test.xml test file containing the URLs used to access each test.
8. (Optionally) generate a login.properties file containing the authentication name/password the harness passes to the
   portal when challenged.
9. Run the FacesBrige TCK test harness by issuing the appropriate maven command.
10. Review the results.
11. Debug problems, fix and retry above until all tests pass.

---

## Procedure for FacesBridge 5.x Certification

This section describes the compatibility testing procedure and compatibility requirements for FacesBridge 5.x certification.

### Certification Overview

-  Install the appropriate version of the FacesBridge TCK and execute it in accordance with the instructions in this
   User’s Guide.
-  Ensure that you meet the requirements outlined in "Compatibility Requirements" below.
-  Certify to the Maintenance Lead that you have finished testing and that you meet all the compatibility requirements.

### Compatibility Requirements

#### Definitions

These definitions are for use only with these compatibility requirements and are not intended for any other purpose.

- Computational Resource: A piece of hardware or software that may vary in quantity, existence, or version, which may be
  required to exist in a minimum quantity and/or at a specific or minimum revision level so as to satisfy the
  requirements of the Test Suite. Examples of computational resources that may vary in quantity are RAM and file
  descriptors. Examples of computational resources that may vary in existence (this is, may exist or not) are graphics
  cards and device drivers. Examples of computational resources that may vary in version are operating systems and
  device drivers.
- Conformance Tests: All tests in the Test Suite for an indicated Technology Under Test, as distributed by the
  Maintenance Lead, excluding those tests on the Exclude List for the Technology Under Test.
- Container: An implementation of the associated Libraries, as specified in the Specifications, and a version of a Java
  SE Runtime Product, as specified in the Specifications, or a later version of a Java SE Runtime Product that also meets
  these compatibility requirements.
- Documented: Made technically accessible and made known to users, typically by means such as marketing materials,
  product documentation, usage messages, or developer support programs.
- Exclude List: The most current list of tests, distributed by the Maintenance Lead, that are not required to be passed
  to certify conformance. The Maintenance Lead may add to the Exclude List for that Test Suite as needed at any time, in
  which case the updated Exclude List supplants any previous Exclude Lists for that Test Suite.
- Libraries: The class libraries, as specified through the Java Community Process℠ (JCP℠), for the Technology Under
  Test. The Libraries for Portlet 3.0 Bridge are listed at the end of this section.
- Location Resource: A location of classes or native libraries that are components of the test tools or tests, such that
  these classes or libraries may be required to exist in a certain location in order to satisfy the requirements of the
  test suite. For example, classes may be required to exist in directories named in a CLASSPATH variable, or native
  libraries may be required to exist in directories named in a PATH variable.
- Maintenance Lead: The JCP member responsible for maintaining the Specification, reference implementation, and TCK for
  the Technology. Liferay Inc. is the Maintenance Lead for FacesBridge 5.x.
- Operating Mode: Any Documented option of a Product that can be changed by a user in order to modify the behavior of
  the Product. For example, an Operating Mode of a Runtime can be binary (enable/disable optimization), an enumeration
  (select from a list of localizations), or a range (set the initial Runtime heap size).
- Product: A licensee product in which the Technology Under Test is implemented or incorporated, and that is subject to
  compatibility testing.
- Product Configuration: A specific setting or instantiation of an Operating Mode. For example, a Runtime supporting an
  Operating Mode that permits selection of an initial heap size might have a Product Configuration that sets the initial
  heap size to 1 Mb.
- Resource: A Computational Resource, a Location Resource, or a Security Resource.
- Rules: These definitions and rules in this Compatibility Requirements section of this User’s Guide.
- Runtime: The Containers specified in the Specifications.
- Security Resource: A security privilege or policy necessary for the proper execution of the Test Suite. For example,
  the user executing the Test Suite will need the privilege to access the files and network resources necessary for use
  of the Product.
- Specifications: The documents produced through the JCP that define a particular Version of a Technology. The
  Specifications for the Technology Under Test can be found later in this section.
- Technology: Specifications and a reference implementation produced through the JCP.
- Technology Under Test: Specifications and the reference implementation for Portlet 3.0.
- Test Suite: The requirements, tests, and testing tools distributed by the Maintenance Lead as applicable to a given
  Version of the Technology.
- Version: A release of the Technology, as produced through the JCP.

#### Rules for Bridge Products

##### PLT1

The Product must be able to satisfy all applicable compatibility requirements, including passing all Conformance Tests,
in every Product Configuration and in every combination of Product Configurations, except only as specifically exempted
by these Rules.

For example, if a Product provides distinct Operating Modes to optimize performance, then that Product must satisfy all
applicable compatibility requirements for a Product in each Product Configuration, and combination of Product
Configurations, of those Operating Modes.

##### PLT1.1

If an Operating Mode controls a Resource necessary for the basic execution of the Test Suite, testing may always use a
Product Configuration of that Operating Mode providing that Resource, even if other Product Configurations do not
provide that Resource. Notwithstanding such exceptions, each Product must have at least one set of Product
Configurations of such Operating Modes that is able to pass all the Conformance Tests.

For example, a Product with an Operating Mode that controls a security policy (i.e., Security Resource) which has one or
more Product Configurations that cause Conformance Tests to fail may be tested using a Product Configuration that allows
all Conformance Tests to pass.

##### PLT1.2

A Product Configuration of an Operating Mode that causes the Product to report only version, usage, or diagnostic
information is exempted from these compatibility rules.

##### PLT2

Some Conformance Tests may have properties that may be changed (currently there are no such tests in this TCK). Apart
from changing such properties and other allowed modifications described in this User’s Guide, no source or binary code
for a Conformance Test may be altered in any way without prior written permission. Any such allowed alterations to the
Conformance Tests would be posted to the Liferay Faces web site and apply to all licensees.

##### PLT3

The testing tools supplied as part of the Test Suite or as updated by the Maintenance Lead must be used to certify
compliance.

##### PLT4

The Exclude List associated with the Test Suite cannot be modified.

##### PLT5

The Maintenance Lead can define exceptions to these Rules. Such exceptions would be made available to and apply to all
licensees.

##### PLT6

All hardware and software component additions, deletions, and modifications to a Documented supporting hardware/software
platform, that are not part of the Product but required for the Product to satisfy the compatibility requirements, must
be Documented and available to users of the Product.

For example, if a patch to a particular version of a supporting operating system is required for the Product to pass the
Conformance Tests, that patch must be Documented and available to users of the Product.

##### PLT7

The Product must contain the full set of public and protected classes and interfaces for all the Libraries. Those
classes and interfaces must contain exactly the set of public and protected methods, constructors, and fields defined in
the Specifications for those Libraries. No subsetting, supersetting, or modifications of the public and protected API of
the Libraries are allowed except only as specifically exempted by these Rules.

##### PLT7.1

If a Product includes Technologies in addition to the Technology Under Test, then it must contain the full set of
combined public and protected classes and interfaces. The API of the Product must contain the union of the included
Technologies. No further subsetting, supersetting, or modifications to the APIs of the included Technologies are
allowed.

##### PLT8

The functional programmatic behavior of any binary class or interface must be that defined by the Specifications.

### Portlet Test Appeals Process

Liferay has a process for managing challenges to its FacesBridge TCK and plans to continue using a similar process
in the future. Liferay Inc., as Maintenance Lead, will authorize representatives from its engineering team to be the
point of contact for all test challenges.

### Process Used to Manage Challenges to Bridge Tests:

The following process will be used to manage challenges to FacesBridge TCK tests:

1. Who can make challenges to the FacesBridge TCK tests?

- Anyone, though you must create a JIRA account to submit challenges.

2. What challenges to the FacesBridge TCK tests may be submitted?

- Individual (or related) tests may be challenged for reasons such as:

  - Test is buggy (i.e., program logic errors).
  - Specification item covered by the test is ambiguous.
  - Test does not match the specification.
  - Test assumes unreasonable hardware and/or software requirements.
  
3. How are challenges submitted?

- By filing a [JIRA ticket](https://issues.liferay.com/projects/FACES). 
- The JIRA form must be filed against the Liferay Faces Bridge project. The "Issue Type" must be "TCK Challenge". The
  Component should be set to "FacesBridge TCK". The "Affects Version" should be set to to version of the TCK that
  contains the test being challenged. The description field must contain the information in the Test Challenge Form below.

4. How and by whom are challenges addressed?

- The Liferay committers responsible for the Liferay Faces Bridge project coordinate the review and decisions made by
  test development and specification development engineers.

- See the FacesBridge TCK Test Appeals Steps below.

5. How are approved changes to the FacesBridge TCK tests managed?

- All tests found to be invalid are placed on the Exclude List for that version of the FacesBridge TCK within 1
  business day. The JIRA is updated with all pertinent information including providing a description of the change and
  where/how to acquire it. In addition, an announcement is sent to jsr-378-public@jcp.org describing the change and
  where/how to acquire it.

- Liferay as Maintenance Lead, has the option of creating an alternative test to address any challenge. Alternative
  tests (and criteria for their use) will be updated in the GitHub repository. Note that passing an alternative test
  is deemed equivalent with passing the original test.

### Portlet TCK Test Appeals Steps

1. A test challenge is submitted using the JIRA system contesting the validity of one or a related set of Portlet tests.

- A detailed justification for why each test should be invalidated must be included with the challenge as described by
  the Test Challenge Form.

2. The Maintenance Lead evaluates the challenge.

- If the appeal is incomplete or unclear, the JIRA is updated request further information or correction. If all is in
  order, the Maintenance Lead will check with the test developers to review the purpose and validity of the test before
  writing a response. The Maintenance Lead will attempt to complete the response within 5 business days. If the
  challenge is similar to a previously rejected test challenge (i.e., same test and justification), the Maintenance Lead
  will update the JIRA accordingly.

3. The challenge and any supporting materials from test developers is sent to the specification engineers for
   evaluation.

-  A decision of test validity or invalidity is normally made within 15 working days of receipt of the challenge. All
   decisions will be documented with an explanation of why test validity was maintained or rejected.

4. The licensee is informed of the decision and proceeds accordingly.

- If the test challenge is approved and one or more tests are invalidated, the Maintenance Lead places the tests on the
  Exclude List for that version of the Portlet (effectively removing the test(s) from the Test Suite). Details
  concerning the exlcusion will be added into the submitted JIRA challenge in the Challenge Response. If the test is valid
  but difficult to pass due to hardware or operating system limitations, the Maintenance Lead may choose to provide an
  alternate test to use in place of the original test (all alternate tests are made available to the licensee community).

5. If the test challenge is rejected, the licensee may choose to escalate the decision to the Executive Committee (EC),
   however, it is expected that the licensee would continue to work with the Maintenance Lead to resolve the issue and
   only involve the EC as a last resort.

#### Test Challenge Form

Replace the values in \[\] with an appropriate response:

- \[Test Challenger Name and Company\] 
- \[Specification Name(s) and Version(s)\] 
- \[Test Suite Name and Version\] 
- \[Exclude List Version\] 
- \[Test Name\] 
- \[Complaint (argument for why test is invalid)\]

#### Test Challenge Response Form

- \[Test Defender Name and Company\] 
- \[Test Defender Role in Defense (e.g., test developer, Maintenance Lead, etc.)\] 
- \[Specification Name(s) and Version(s)\] 
- \[Test Suite Name and Version\] 
- \[Test Name\] 
- \[Defense (argument for why test is valid)\] 
- \[Implications of test invalidity (e.g., other affected tests and test framework code, creation or exposure of
  ambiguities in spec (due to unspecified requirements), invalidation of the reference implementation, creation of
  serious holes in test suite)\]
- \[Alternatives (e.g., are alternate test appropriate?)\]

### Reference Implementation for FacesBridge 5.x

The Designated Reference Implementation for compatibility testing of FacesBridge 5.x is as follows:

-  Reference Implementation done as an Open Source Project, Liferay Faces Bridge 5.x
-  Java™ 2 Platform, Standard Edition (Java SE™) Version 8.x

### Specification for FacesBridge 5.x

The following web sites contain the Specifications for FacesBridge 5.x:

http://www.jcp.org/en/jsr/detail?id=378

### Libraries for FacesBridge 5.x

The following packages constitute the required class libraries for FacesBridge 5.x:

- javax.portlet.faces
- javax.portlet.faces.annotation
- javax.portlet.faces.component
- javax.portlet.faces.event
- javax.portlet.faces.filter
- javax.portlet.faces.preference

---

## Hardware & Software Requirements

This section lists the required hardware configurations and prerequisite software that must be present before you can
run the FacesBridge TCK.

### Hardware Requirements

The following section lists the hardware requirements for both the TCK and the reference implementation. Hardware
requirements for other implementations will vary.

All systems must meet the following recommended and minimum hardware requirements:

-  CPU running at 500 MHz minimum
-  512MB of RAM minimum
-  1024MB of swap space minimum
-  512 MB of free disk space minimum for writing data to log files
-  Network access

### Software Requirements

The FacesBridge TCK test harness relies on Selenium WebDriver to execute the tests/process the results. 

Browsers:

- Chrome

In addition to build and run the TCK you must have the following Java™ software installed:

-  Java EE™ 7 Platform, JDK: Version 8.x
-  Vendors application server of choice with the minimum requirement that the application server contain a Servlet
   container.
-  Java™ Portlet 3.0 (compatible) platform software, such as a vendor’s implementation
-  JavaServer™ Faces 2.2 platform software (if not already included in the application server). If using Mojarra,
   Version 2.2.20 or later.
-  Java™ consumer application which can host/expose test portlets in individually addressable URL resources (such as
   a portal)

Finally, as the TCK is built in run within a local version of the Apache project, you will need to install:

*  Apache Maven

---

## Building and Running the FacesBridge TCK

The following commands show how to build the FacesBridge TCK from source. Note that Maven profiles are used in order to
build the WAR modules for the target portlet container (e.g. pluto or liferay). You will likely need to modify the
pom.xml descriptors to support your vendor specific portlet container.

    git clone https://github.com/liferay/liferay-faces-bridge-impl.git
    git checkout 5.x
    cd tck
    mvn -P pluto,mojarra,tomcat,thin clean build
    cp bridge-tck-cdi1-portlet/target/*.war $PORTLET_CONTAINER_DIR
    cp bridge-tck-cdi2-portlet/target/*.war $PORTLET_CONTAINER_DIR
    cp bridge-tck-flows-portlet/target/*.war $PORTLET_CONTAINER_DIR
    cp bridge-tck-lifecycle-set-portlet/target/*.war $PORTLET_CONTAINER_DIR
    cp bridge-tck-main-portlet/target/*.war $PORTLET_CONTAINER_DIR
    cp bridge-tck-scope-portlet/target/*.war $PORTLET_CONTAINER_DIR

The following commands will invoke the FacesBridge TCK harness for automated testing via Selenium:

    cd bridge-tck-main-portlet
    mvn -Dintegration.port=9080 verify -P selenium,pluto,chrome,tomcat "$@" -Dintegration.browser.headless=false

---

## Apache Pluto

This section discusses topics associated with running the FacesBridge TCK within Apache Pluto.

### Placing FacesBridge TCK on Pluto Portal Pages

The pluto-portal-driver-config.xml file that comes supplied with the FacesBridge TCK defines the test portlets and
associated portal pages.

### Issues with running the TCK in a Pluto Environment

Many TCK tests verify portlet behavior in a JSF environment and therefore rely on the underlying portlet container to be
providing bug free JSR 362 (spec) support. In addition, the JSR 362 specification gives the portlet container
implementor a certain amount of leeway in implementing certain features. In the case of Pluto, a number of TCK fail
though the test itself is correct and has been verified on other containers. The bulk of the failures relate to the set
of event tests listed below. These tests only fail when driven from the test harness. When configured and run
individually or in small groups within the Pluto portal they run successfully. The failure is the result of the TCK
using a common test event in each of the event tests. Though the harness expects to run each test on a different page
(consumer url), because all the pages are configured and Pluto doesn't recognize that non-active (visible) portlets
don't need to be rendered, the event tests fail because the render url that Pluto generates following the dispatch of
the event includes all the event recipients. Because of the number of event tests, the render url runs into (Apache)
maximum length issues.

This is a list of the event tests that fail when running the TCK via the test harness in Pluto:

- redirectEventTest
- getResponseContentTypeEventTest
- getResponseCharacterEncodingEventTest
- eventDestroyTest
- getRequestContentTypeEventTest
- getRequestCharacterEncodingEventTest
- getRequestHeaderValuesMapEventTest
- getRequestHeaderMapEventTest
- encodeActionURLWithInvalidWindowStateEventTest
- encodeActionURLWithWindowStateEventTest
- encodeActionURLWithInvalidModeEventTest
- encodeActionURLWithModeEventTest
- encodeActionURLWithParamEventTest
- encodeActionURLJSFViewEventTest
- portletPhaseRemovedEventTest
- facesContextReleasedEventTest
- eventControllerTest
- eventScopeNotRestoredModeChangedTest
- eventScopeNotRestoredRedirectTest
- eventScopeRestoredTest
- eventPhaseListenerTest
- isAutoDispatchEventsNotSetTest
- isAutoDispatchEventsSetTest

Its recommended you remove these tests from the test.xml before running the harness and then testing them manually by
rolling back Pluto to its standard (page) configuration and then adding, running, removing each portlet one at a time
from a test page using the Pluto Admin tab.

There is one test that fails when run in Pluto whether run as part of the set via the test harness or run manually
because of a bug in Pluto. It is setRequestCharacterEncodingActionTest.

The destroyXXXTest(s) can only be run successfully once per portlet activation (i.e. the application containing this
test and/or the server must be bounced before this test can run successfully again). If you run the test again an error
will be reported though it will indicate that the prior run was successful.

---

## Debugging Test Problems

If any of the tests should fail descriptive information describing the nature of the failure is output by the test and
included in the generated report. If this description doesn't provide enough information to determine the cause of the
failure, the bridge will need to be debugged. As the bridge is part of a portlet application, debugging occurs using
exactly the same procedure used to debug a portlet application in the test server environment. As different test servers
may have different procedures consult the documentation of yours for details.

Often when tracking down an issue its helpful to isolate the tests run to only the test at issue. If using the TCK Test
harness to execute the tests, you can accomplish this by commenting out or removing those tests in your test.xml file
that you don't want to have run. Another, often easier, alternative is to merely to run the test manually by invoking
the page containing the test at issue directly from your browser (and then clicking on any of the indicated buttons if
provided).

Because the primary function of the Bridge is to translate between the Portlet and JSF environments, one common cause
for test failures is a bug either in the portlet container or the Faces implementation (more commonly the portlet
container). If you isolate the issue to a source outside the bridge then to pass the TCK you will need to verify bridge
correctness (for the failed tests) on a different platform.

---

## Frequently Asked Questions

Q1: Where do I start to debug a test failure? 

A1: Check the detailed description concerning the failure contained in the test run report. If this doesn't provide
enough information to resolve the problem you will need to attach a debugger to the server and inspect its behavior.

Q2: Where is the TCK's text exclusion list located? 

A2. It is in tck/bridge-tck-harness/src/main/resources/excluded-tests.xml

Q3: Why are there tests in the Excludes File? 

A3: The test exclusions file contains all tests that are not required to be run to certify your Bridge. In general
excluded tests are those which were found for one reason or another not to operate correctly in certain specific
environments due either to a JSF implementation dependency in the test or because it verifies behavior that the
underlying portlet container isn't required to support.. Each test in the test exclusions file contains a comment
detailing under what circumstance the test can and can't be run. If you are running in an environment which supports the
test, you are strongly encouraged to manually run the test to ensure this portion of the bridge function is operating
correctly.

Q4: Should I run any tests that are in the Excludes File? 

A4: Each test in the test exclusions file contains a comment detailing under what circumstance the test can and can't be
run. If you are running in an environment which supports the test, you are strongly encouraged to manually run the test
to ensure this portion of the bridge function is operating correctly.

Q5: All the TCK events tests fail in my environment when run via the test harness. Why? 

A5: As discussed in the chapter related to running the TCK in a Pluto environment, a consumer needs to render itself
following dispatching events to its recipients. Some consumers will generate a (render) url that encodes the need to
render every recipient regardless of whether that portlet is visible (on the current page). This can cause problems when
the underlying servlet container and or web server limits the size of (received) urls. You may be running into the same
issue. Try configuring each portlet separately, running the test manually, and then removing it from the portal to
verify correctness.

Q6: A test fails because of a portlet container bug. Should I challenge the test and request it be excluded? 

A6: No. Its likely the test is valid and hence you will need to verify your Bridge passes in a different environment.
