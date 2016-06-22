# aadl2uppaal

RTL Test Generator From SystemC TLM Specifications
Version: 0.1


Our tool is a framework that can automatically generate both TLM testcase and 
RTL testcase from the input SystemC TLM specification.

The tool has tree parts: i) TLM2SMV translator, ii) TLM test generator, and
iii) RTL test translator from TLM tests.

* TLM2SMV Translator:
     Input: Pre-processed SystemC TLM code.             
     Output: SMV formal model, required properties  and testcase format

     A translator that can transform the "pre-processed" SystemC code into 
     SMV formal model. We pre-process to eliminate the features that are
     not currently supported. For example, we remove/ignore namespace 
     definition, include file, timing specification etc. The output has two 
     parts: i) the smv model and the corresponding properties for test
     generation; ii) the testcase format which has the necessary information 
     for the test generation, such as the data type of the testcase.

* TLM_TEST_GEN -- TLM test generator
     Input: A property and & testcase format    
     Output: TLM testcase
    
     The step invokes SMV model checker with the input proeprty (negated
     version) to generate the counterexample. The generated counterexample
     is analyzed to extract the TLM testcase based on the testcase format.

* TLM_TEST_2_RTL_TEST -- TLM to RTL test translator
     Input: TLM testcase & TLM to RTL interface specification    
     Output: RTL testcase
    
     This step translates TLM testcase to the RTL level testcase according 
     to the TLM to RTL interface specification.

## The running environment:

   We have tested this framework under the following environments. However,
   it is expected to work under Solaris as well.

    a) Operating System: Linux 2.6.24
    b) Pre-installed software:
       * Bison 2.3
       * flex 2.5.34
       * JAVA 1.6.0
       * GCC 4.2.3 (include g++)
       * Perl 5.8.8
       * Candence SMV (linux version). 
         Can be downloaded from http://www.kenmcmil.com/

## The structure of the current folder:

   The current folder (TLM2RTL) has the following five directories and three
   files.
    * README: this file.
    * TLM2SMV: The translator which can translate the SystemC TLM file to the 
      SMV file. It also generates the format for TLM testcase extraction.
    * TLM_TEST_GEN: Generate the TLM testcase using SMV model checker and
      counter-example analysis.
    * TLM_TEST_2_RTL_TEST : Translate the TLM testcase to RTL testcase by 
      using the pre-defined tranlation interface rules.
    * Makefile: Build the whole project.
    * run.pl: You can use it to run the translation.
    * Example: This directory contains the original router example (esl_latest)
      as well as the pre-processed router.tlm example. It also contains
      the interface specification that is needed for TLM 2 RTL test translation.
    * SMV: this directory contains SMV model checker.

## Usage:

     For the usage of each part individually, please check the README files under each sub-folder.     

     1) Start fresh
        > make clean

     2) Build the whole project
        > make all
    
        It will create the "bin" folder containing all the executable binaries 
        for this project. 

     3) Setup paths SMV model checker 
        > setenv LD_LIBRARY_PATH <TLM2RTL_directory>/SMV/lib:$LD_LIBRARY_PATH
        > set path =(<TLM2RTL_directory>/SMV/bin:$path)

     4) Run the program.
  
         > perl run.pl <folder_name> <tlm_file> <tlm_test_format> <module_name> <variable_name> <rtl_spec>
 
       - folder_name: The folder that contains both TLM file and TLM2RTL 
         translation rules (interface specification)
        - tlm_file : The pre-processed SystemC TLM file.
       - tlm_test_format: The necessary information for the TLM test genertor. 
       - module_name: The module that you are trying to test.
       - variable_name: which variable the testcase corresponds to.
       - rtl_spec: The specification for TLM to RTL testcase translation.

     For example, when running our router example, use the following command:
       >  perl run.pl Example router.tlm packet d0 p router_spec.txt

    This program will generate a folder whose name is same as the prefix of 
    the SystemC TLM file. For instance, in the router example, the folder
    "router" is created inside the "Example" directory. There are two 
    sub-folders under this folder (tlm_test and rtl_test), one contains all 
    TLM testcases, and the other one contains the corresponding RTL testcases.

## License

Dual licensed under GPL and MIT licenses.

Copyright (c) 2010 [Louis-Rémi Babé](http://twitter.com/louis_remi).
'Developper: Mingsong Chen
 Organization: University of Florida
 Date: Oct. 22, 2008'