# Application Project Group 7

This file encomposes documentation showing how to
run the application project for group 7 in CS-643-851 Fall 2018.

## Table of Contents
* [Overview](#overview)
* [Download](#download)
	- [Download using Git](#download-using-git)
	- [Download as a Zip](#download-as-a-zip)
* [Directory Structure](#directory-structure)
	- [faces](#faces)
	- [pom.xml](#pom.xml)
	- [src](#src)
	- [Vagrantfile](#vagrantfile)
* [Modules](#modules)
* [Test the Application](#test-application)
	- [Virtual Machine](#virtual-machine)
		+ [Log in using ssh](#log-in-using-ssh)
		+ [Log in using the GUI](#log-in-using-the-gui)
		+ [Destroy Virtual Machine](#destroy-virtual-machine)
	- [Build the Application](#build-the-application)
		+ [Build using Command Line](#build-using-command-line)
		+ [Build using Eclipse](#build-using-eclipse)
	- [Resize and Greyscale Images](#resize-and-greyscale-images)
		+ [Resize and Greyscale Images using AWS Lambda](#resize-and-greyscale-images-using-aws-lambda)
		    * [Create IAM Role](#create-iam-role)
		    * [Create S3 Buckets](#create-s3-buckets)
		    * [Create Lambda Function](#create-lambda-function)
		    * [Run Lambda Function](#run-lambda-function)
		+ [Resize and Greyscale Images using Virtual Machine](#resize-and-greyscale-images-using-virtual-machine)
	- [Append Values to Log](#append-values-to-log)
	- [Classify Images](#classify-images)
	- [Common Issues](#common-issues)
		+ [Troubleshoot Missing Files](#troubleshoot-missing-files)
* [Presentation](#presentation)

## Overview
The overall objective of this application is to train a machine learning model
in order to accurately classify the gender of a person in an image 
using distributed and parallel techniques.

The application works as the following.
Users will upload facial images,
which AWS Lambda will then resize, greyscale, and process.
Then, the red, blue, green (RGB) values of the resulting images will be appended to a log,
along with the filename of each image
and the genders of the person in each image,

A gender of female is represented as 0,
and a gender of male is represented as 1.

The log is formatted by separating each field by a comma.
In other words, the log is a CSV file.
One can represent the log as the table seen below.

| Gender             | Filename             | RGBs             |
| :----------------: | :------------------: | :--------------: |
| gender<sub>1</sub> | filename<sub>1</sub> | RGBs<sub>1</sub> |
| gender<sub>2</sub> | filename<sub>2</sub> | RGBs<sub>2</sub> |
| ...                | ...                  | ...              |
| gender<sub>n</sub> | filename<sub>n</sub> | RGBs<sub>n</sub> |

The classifier will then read the log
in order to train a machine learning model using Apache Spark
so that the model can classify the gender of a person in an image
who is unknown.
Currently, the classifier uses the support vector machine algorithm
with stochastic gradient descent as its optimizer.
The classifier also currently takes the facial images
and randomly splits the data 70/30,
where 70% of the data is for training,
and the remaining 30% is for testing.

Both AWS and VirtualBox can be used to run the application.
AWS will be used in order to configure a multi-node cluster
in order to measure performance,
and everyone may not have access to AWS,
so we have also provided a virtual machine
which can be created and provisioned with Vagrant.
The steps needed to set up the virtual machine are provided within this document.

## Download
All associated source code will be submitted in the submission link for the assignment,
but if one wants to download the latest source code,
one can do so using [git](#download-using-git).
One also has the choice of downloading the source code
in a [zip](#download-as-zip) file.

#### Download using Git
If one would like to clone the repository using git,
but one does not have git installed,
first download [git](git).
Then using the command below,
one can download the source code.

```
git clone https://gitlab.com/jlosito/spark.git
```

#### Download as Zip
If one would like to download all of the associated source code in a zip file,
use the following [link](zip).

## Directory Structure
This section describes important sections of the directory structure found in this project.

```
.
+-- faces
    +-- faces94
    +-- faces96
+-- pom.xml
+-- src
    +-- main
        +-- java
	    +-- classifier
	        +-- Classifier.java
	    +-- class-logger
	        +-- ClassLogger.java
		+-- log-image-class.sh
	    +-- image-modifier
	        +-- ImageModifier.java
	+-- shell
	    +-- random-test
	        +-- random-test.sh
+-- Vagrantfile
```

#### faces
This directory contains the [faces](faces) dataset.

#### pom.xml
This file defines how to build the classifier using Maven.

#### src
This directory contains the source code of each of the [modules](#modules) of the project.
For instance, the subdirectory named classifier
contains the module which classifies the images.

#### Vagrantfile
This file defines a [virtual machine](#virtual-machine)
using VirtualBox as the hypervisor
so that one can test the project.

## Modules
This application consists of several modules.
Each module performs a separate function.
For instance, one module resizes the images,
and another module trains the machine learning model.
Some modules can only be ran using AWS services such as Lambda.
Therefore, we've provided an equivalent module
that can be ran on the virtual machine.

Note that there are some modules which are not used
in the latest version of the project,
but were used in the early development stages.
These modules which are not used will eventually be removed.

The application consists of the following modules.

* ImageModifier.java: resizes and greyscales all images in a directory
* ClassLogger.java: appends the information of an image to a log
* log-image-class.sh: driver which logs each image to a file using ClassLogger
* random-test.sh: script to randomly chooses images to use as testing data
* Classifier.java: classifies gender of person in image using Apache Spark

## Test the Application
This section describes the steps required 
in order to test the application 
using a virtual machine locally.

#### Virtual Machine
A virtual machine is provided with the project using the file named Vagrantfile.
This is done so that one can test the project locally on one's desktop/laptop
without having to recreate the project using AWS EC2.
The virtual machine should already include all tools needed in order to build and test the application.
For instance, the virtual machine comes with Java, Maven, and Apache Spark already installed and configured.

The configuration of Apache Spark used in this virtual machine is configured in a pseudo-distributed manner,
where the client, master, and workers are all running on the same node.

One should not have to do anything additional in order to start the processes associated with Apache Spark.
In other words, all of the processes should be already running and ready to accept jobs
once one has created and started the virtual machine.

In order to make use of the virtual machine,
one first needs to download both [VirtualBox](virtualbox)
and [Vagrant](vagrant).

Once VirtualBox and Vagrant is installed and the project is downloaded,
note that the virtual machine is currently configured to use 2048 GB of memory
and 2 CPUs.
If one wants to use more memory or cores,
adjust the following lines in the file named Vagrantfile.

```
vb.memory = 2048
vb.cpus = 2
```

Now, one can execute the following command while in the project's root directory
in order to create and provision the virtual machine.

```
vagrant up
```

Go and get some tea or coffee. 
Provisioning the virtual machine may take awhile.

Once the script has completed, 
you can either log into the virtual machine either using [ssh](#log-in-using-ssh) 
or through the [GUI](#log-in-using-the-gui). 
Using either method, the word "vagrant" is both the username and password.

#### Log in using ssh
If one would like to ssh into the virtual machine,
use the following command.

```
vagrant ssh
```

#### Log in using the GUI
If one would like to use the GUI in order to log into the virtual machine,
first open the VirtualBox application.
There one will see a virtual machine called spark
which will be in the running state.
Double click this machine in order to log in.

#### Destroy Virtual Machine
Once one has tested the application
or one no longer requires the virtual machine,
using the following command,
one can destroy the virtual machine.

```
vagrant destroy --force
```

#### Build the Applicaiton
Once a user has logged into the virtual machine,
one needs to build the project using Maven.
<!--If one is comfortable building the project from the command line, lambda function.
go to the [Build Application using the Command Line](#build-application-using-the-command-line) section.-->

#### Build using the Command Line
Make sure one's current directory is the subdirectory named vagrant under the root directory
using the command below.

```
cd /vagrant
```

Vagrant should automatically synch the files associated with the project
and the virtual machine.
Thus, one should be able to view all of the files when using the command below
in the directory above.

```
ls
```

If one is unable to see the files,
go to the [Troubleshoot Missing Files](#troubleshoot-missing-files) section.

If everything looks good,
then one should be able to build the project
using the following commands below.

```
mvn clean package
```

#### Resize and Greyscale Images
This application has the ability to resize and greyscale the facial images using either of the following two methods.

* [AWS Lambda](#resize-and-greyscale-images-using-aws)
* [Virtual Machine](#resize-and-greyscale-images-using-virtual-machine)

#### Resize and Greyscale Images using AWS Lambda
This section describes how one can resize and greyscale the facial images
using AWS Lambda.
In order to do so, 
one must first perform the following steps.

1. [Create IAM Role](#create-iam-role)
2. [Create S3 Buckets](#create-s3-buckets)
3. [Create Lamda Function](#create-lambda-function)

Then, once one has completed those steps,
one can test the lambda function.

#### Create IAM Role
This section describes how one can create an IAM role.

1. Login AWS Console
2. Click on services at the top menu
3. Go to IAM and create a role
4. Select lambda
5. On permission policy select awslambdafullaccess
6. Click next on "tags" page
7. Input a name for the Role
8. Click create role

#### Create S3 Buckets
This section describes how one can create two S3 buckets.
One bucket will be used to upload images,
and the other bucket will be used to store the modified images.

1. Click on services at the top menu
2. Go to S3
3. Click on Create Bucket
4. Input "name" for Bucket
5. Select region click next(region must be same as lambda function)
6. On "configure options" click next
7. On "Set permissions" click next
8. On "Review" click create

For 2nd Bucket follow steps from above and name 2nd bucket the same name as 
1st bucket with "resized" at the end. 
For example, if 1st bucket's name is imagebucket
the 2nd bucket will be named imagebucketresized.

#### Create Lambda Function
This section describes how one can create a lambda function.

1. Click on services at the top menu
2. Go to lambda
3. Click on create function (region must be same as S3)
4. Select "Author from scratch"
5. For "Name" input name for function
6. For "Runtime" select Java 8
7. For "Role" select choose an existing role
8. For Role Select name of IAM role created above
9. Click create function
10. Click on Designer
11. Click on function name to configure it
12. Scroll down to where it says "Code entry type" select upload .zip or .jar
13. Click on upload select jar file>click open
14. For "runtime" select Java 8
15. For "Handler" type the following: example.Hello::handleRequest
16. Click Save
17. Go to "Add triggers" select S3
18. Scroll down to configure triggers
19. On "Bucket" select S3 bucket create from previous step
20. On Event type select Object Created(All)
21. skip Prefix
22. on Suffix type ".jpg"
23. check box "Enable trigger"
24. Click Add
25. Scroll up click save
26. Note: Go to to "add trigger" and select S3 again
27. Repeat steps from first bucket configuration but change the suffix to ".png" instead of ".jpg"

Note: Follow same steps to add more triggers to the suffixs for example: .PNG, .JPG, .gif, .GIF

#### Run Lambda Function
Now, one run the lambda function.

1. Go to services on top menu
2. Select S3
3. Select name of Bucket where original images will be stored
4. Click upload> Select image to be modified>After upload is complete
5. Go to bucket named resized and you will see the modified image

#### Resize and Greyscale Images using Virtual Machine
If one wants to resize and greyscale the images using the virtual machine,
one first needs to compile the Java program using the command below.

```
javac src/main/java/image-modifier/ImageModifier.java
```

Then, using the commands below,
one can modify the images.

```
cd src/main/java/image-modifier

# modify male images
mkdir male
java ImageModifier ../../../../faces/faces94/male
mv *.jpg male

# modify female images
mkdir female
java ImageModifier ../../../../faces/faces94/female
mv *.jpg female

# remove the leftovers
rm *.gif
rm *.wmd

# setup the directories
mv male ../../../../
mv female ../../../../

cd ../../../../
```

#### Append Values to Log
Once, the images have been resized and grescaled,
one must append the values to a log using the following command.

```
cd src/main/java/class-logger

# build the program
javac ClassLogger.java

# run the program
chmod 700 log-image-class.sh
./log-image-class

cd ../../../../
```

This may take awhile...

#### Classify Images
In order to classify the images,
run the command below.

```
spark-submit target/classifier-0.0.1.jar out.csv
```

If one wants to save the model,
use the optional argument
where "${model_dir}"
represents the folder where one wants to save the model.

```
spark-submit target/classifier-0.0.1.jar out.csv ${model_dir}
```

#### Common Issues
This section lists some common issues which one may face 
when testing this application.
If one does not find the solution within this document for a problem one is having,
please contact the following people below
regarding the section one is having problems with.

* Virtual Machine & Apache Spark Program: John Losito <lositojohnj@gmail.com>
* AWS Lambda: Edwin Rondon <emr7@njit.edu>
* AWS Multi-node Configuration and Performance: Manas Bhandarkar <mvs34@njit.edu>

#### Troubleshoot Missing Files
If one is unable to view the associated files of the project in the virtual machine,
one may need restart the virtual machine using the following command from the host machine.

```
vagrant reload
```

# Presentation
The presentation slides can be viewed [here](presentation slides).

[git]: https://git-scm.com/downloads
[zip]: https://gitlab.com/jlosito/spark/-/archive/master/spark-master.zip
[faces]: https://cswww.essex.ac.uk/mv/allfaces/
[virtualbox]: https://www.virtualbox.org/wiki/Downloads
[vagrant]: https://www.vagrantup.com/downloads.html
[presentation slides]: https://docs.google.com/presentation/d/1xOf83XfPNg654YAwPZPH7ZDGywAomt--WmLil8E-TQc/edit?usp=sharing
