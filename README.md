# A Tool-Chain for Quantitative Evaluation of Uncertainty-Aware Hybrid AADL Designs

This project provides a tool-chain that integrates the open-source AADL tool environment OSATE, NPTA model generator and UPPAAL-SMC to enable the automated performance evaluation and comparison of uncertainty-aware Hybrid AADL designs against various performance queries.

## New Features
- Our Uncertain Hybrid AADL environment supports three kinds of annexes, i.e., **[BLESS](http://bless.santoslab.org/)**, **[Hybrid Annex](http://www.santoslab.org/pub/bless/papers/ha-hilt2014.pdf)**, and our proposed **Uncertain Annex**. Based on these annexes, our tool chain enables the stochastical modeling and quantitative analysis of cyber-physical systems under uncertain environment. 
- Our tool-chain supports the modeling of **non-linear hybrid systems**. 
- The **Uncertain Annex** supports the accurate modeling of both performance variations caused by uncertain environments and performance requirements specified by designers. The designers can use Uncertain annex to describe their uncertain objects using a wide spectrum of programmable distributions (e.g., Gaussian, uniform, Poisson and exponential distributions).
- The tool-chain supports the **automated translation** of AADL designs as well as designers’ requirements into Networks of Priced Timed Automata (NPTA) and performance queries, respectively.
- The tool-chain supports the **quantitative evaluation** of Uncertain Hybrid AADL designs against various properties (e.g., performance and safety queries) based on statistical model checker UPPAAL-SMC.


## Dependencies:

Our tool-chain consists of three key components: modeling tool, AADL translation tool, and quantitative evaluation tool. All these tool are all developed using JAVA. So users should have a JAVA Runtime environment. We suggest the users to install the JAVA Runtime environment **[JAVA 1.8.0](http://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html)**.

- **Modeling Tool:** In our tool-chain, we adopt an open-source tool **[OSATE 2.2.1](http://aadl.info/aadl/osate/stable/2.2.1/products/)** for the AADL modeling. To enable the modeling of discrete and continuous behavior modeling, we need to install the **[BLESS plugin](http://www.santoslab.org/pub/bless/downs/tool/edu.ksu.bless.update.zip)** for OSATE. Note that the latest version of **BLESS Annex** supports the syntax checking of  **Hybrid Annex**. Since OSATE is an open-source tool that is maintained by other developers, we do not include it in this project

- **Model Translation Tool:** Our model translation tool is developed using JAVA. It consists of three key parts : i) an XMI parser that can extract the necessary AADL information based on our defined meta-models, ii) a translator based on our one-to-one mapping that can convert the extracted inforamtion into their NPTA counterparts, iii) an NPTA model generator that can generate the NPTA models and corresponding performance queries in XML format. 


- **Evaluation Tool:** In our tool-chain we use the statistical model checker UPPAAL-SMC for the quantitative evaluation of the translated NPTA models. Since this tool is maintained by Uppsala University and Aalborg University, we do not include it in this project. The users can download the **[UPPAAL 4.1.19](http://www.it.uu.se/research/group/darts/uppaal/download/)** from its official site. 


We conducted the experiment using the tool chain under the following environments. We hope it can work under other operating systems and tool versions as well.

- Operating System: Linux 2.6.24 
- Pre-installed software:
  * [OSATE 2.2.1](http://aadl.info/aadl/osate/stable/2.2.1/products/)
  * [BLESS plugin](http://www.santoslab.org/pub/bless/downs/tool/edu.ksu.bless.update.zip) for OSATE (with Hybrid Annex syntax support).
  * [JAVA 1.8.0](http://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html)
  * [UPPAAL 4.1.19](http://www.it.uu.se/research/group/darts/uppaal/download/)

## Structures:
```
src
├── aadl2upaal
│   ├── Application.java (main function)
│   ├── aadl
│   ├── parser
│   ├── upaal
│   └── visitor
└── examples
    ├── CTCS_MA
    │   ├── MA_with_U_uppaal.xml
    │   └── MA_with_uncertainty.xml
    └── Bouncing_Ball
    	├── Ball_AADL.xml
        └── Ball_UPPAAL.xml
```
   This project contains all the source files for the translation from Uncertain Hybrid AADL designs to NPTA models as well as two illustrative case studies. The ``aadl2upaal`` directory contains source code for translation. It has the one file (contains the main function) and four sub-directories.
   
* ``Application.java ``: The JAVA file that contains the main function of our translation tool.
* ``aadl ``: The sub-directory contains all the meta-models of Uncertain Hybrid AADL. The meta-models are implemented using JAVA classes. 
* ``parser ``: The sub-directory contains a parser that can concert AADL models in XMI format into meta-model instances of AADL and annexes.
* ``upaal ``: The sub-directory contains all the meta-models for NPTA designs. The NPTA meta-models are implemented using JAVA classes.
* ``visitor ``: The sub-directory contains the JAVA files for the translation from Uncertain Hybrid  AADL designs to NPTA models.  The file **Transform2U** defines all the one-to-one mapping transformation rules from AADL designs to NPTA models. The file **UppaalWriter** is used to generate NPTA models in XML format. The generated NPTA models can be directly evaluated by the tool UPPAAL-SMC.  


To evaluate the efficacy of our tool-chain, we also include two case studies in the sub-directory named ``examples``. For each case studies, the subsub-director contains two XML files. One is for the Uncertain Hybrid AADL design, and the other is the NPTA model generated by our tool-chain. The details are as follows:

* ``CTCS_MA ``: This subsub-directory contains the system model of **CTCS-3 MA** scenario named ``MA_with_uncertainty.xml`` and the automatically generated NPTA model named ``MA_with_U_uppaal.xml ``. 
* ``Bouncing_Ball ``: The XML file ``Ball_AADL.xml`` is the Uncertain Hybrid AADL model of the Bouncing Ball example and the XML file ``Ball_UPPAAL.xml`` is the generated NPTA model for the evaluation.




## Usage:

To demonstrate the usage of our tool-chain, we use the CTCS-3 MA as an example. We assume that you have installed all the dependent software as mentioned before. Typically, the usage of our tool-chain involves following steps：

1. **Modeling** : You can use the OSTAE tool to model your Uncertain Hybrid AADL designs with the BLESS, Hybrid and Uncertain annexes. Since we have done the modeling of the CTCS-3 MA scenario,  in this example you can directly load the AADL model named ``MA_with_uncertainty.aadl`` in OSATE. If you want to extend the model, you can modify the AADL design.
2. **Model Translation** :
	- Export the AADL model designed in step 1 into an XML file. In this example, the XML file ``MA_with_uncertainty.xml`` in the subsub-directory ``eamples/CTCS_MA`` is the AADL model of the CTCS-3 MA example.
	- Since our project only provides the source code for the model transformation from AADL to NPTA, we need to firstly compile such files to get the NPTA generator. You can compile this  project using a JAVA IDE (e.g., Eclipse, Intellij IDEA) or using the following JAVA command:
	```	
	javac src/aadl2upaal/Application 
	```	
	- You can generate the NPTA model for the CTCS-3 MA AADL design using the  following command:
	 ```
	java Application src/examples/MA_with_uncertainty.xml
	  src/examples/MA_with_U_uppaal.xml
	```
3. **Quantitative Evaluation** :
	- Open the generated NPTA model from step 2 using UPPAAL-SMC.
	- Go to the ``Verifier`` panel and select one performance query from the property list. Now you can start the performance evaluation of the CTCS-3 MA design. 

## License
Copyright © 2016 by Yongxiang Bao and Mingsong Chen. All rights reserved.

For more details at [license](LICENCES.txt).

