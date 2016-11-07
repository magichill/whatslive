#!/bin/bash
SCRIPT_HOME=$(dirname $(readlink -f $0))
bash -ex $SCRIPT_HOME/startup.sh
