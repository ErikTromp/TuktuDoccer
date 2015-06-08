# TuktuDoccer
Automatically create Tuktu documentation on generators and processors from the modeller configs.

The doccer can be run through SBT by specifying two arguments.

  - The folder of the tuktu modeller's meta configuration folder. The folder containing the JSON files that define the configuration for the modeller.
  - The output folder where the MarkDown documentation output should end up in.

Example command to run:

`sbt "run /path/to/modeller/meta /path/to/output/folder"`