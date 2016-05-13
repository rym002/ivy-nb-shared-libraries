# Introduction #

Netbeans module providing Ivy integration in Java and Web projects by exposing Ivy confs as shared libraries. Each conf is resolved and created as a library. The libraries can then be added to the project classpaths.

## Features ##
  * Resolve Ivy confs to Netbeans Shared Libraries
  * Clean Ivy Cache(s)
  * Auto Complete in ivy.xml
  * Auto Complete in ivysettings.xml
  * Filters out jars for sub projects that are ivy enabled.

## Supported Netbeans Project Types ##
  * Java
  * Web

## Options ##
  * Located under Tools | Options | Ivy

### Settings Templates ###
Settings Templates control how resolved artifacts are added to the library. Artifacts can be added as jars, sources or javadocs. Global Templates can be defined under Netbeans Options. Projects can choose a template or specify project specific preferences.
  * Jar Types
    * Accepted types added as jars
  * Source Types
    * Accepted types added as sources
  * Source Suffixes
    * Accepted Suffixes added as sources
  * Javadoc Types
    * Accepted types added as Javadocs
  * Javadoc Suffixes
    * Accepted Suffixes added as Javadocs

## Project Properties ##
An Ivy Category is added to the Project Properties allowing project specific settings.
  * Enable Ivy
    * Enables or disables Ivy for the project
  * Files Tab
    * Ivy File (Required)
      * Location of the ivy.xml file
    * Settings File (Optional)
      * Location of the ivysettings.xml file
    * Properties Files (Optional)
      * Multiple Properties Files needed to resolve variables in ivy.xml or ivysettings.xml
  * Resolution Tab
    * Auto Resolve
      * Resolves/Refreshes Ivy Libraries
        * On Project Open
        * On Ivy File Change
        * On Ivy Settings File Change
        * On Ivy Properties File Change
    * Use Cache Path
      * Uses the cache path for the downloaded artifacts. When unchecked the artifacts will be resolved to the shared libraries folder and the relative path will be used.
    * Global Retrieve Settings
      * Enables/Disables selecting a Settings Template defined under Options
  * Configurations Tab
    * All
      * Resolve all configurations
    * Select Configurations to resolve
## Usage ##
  * Manual Resolve
    * Right Click On the Project and Select Ivy | Resolve
  * Clean Cache
    * Right Click On the Project and Select Ivy | Clean Cache
