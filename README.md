# clj-haddash

Experimental lightweight hadoop dashboard.  Prints ascii visualization of last n jobs run(ning) of hadoop cluster. 0.20.203

#### Supported Hadoop Versions
| Version  |
|----------|
| 0.20.203 |


## Installation

Clone repo

## Usage

```bash
Usage: lein run -m haddash.client <HOST> <PORT> <N-RECORDS> <SCALE-FACTOR>

Description:
 Prints visualization of last n jobs run(ning) on hadoop cluster.

Required:
 HOST		Jobtracker host
 PORT		Jobtracker port
 N-RECRODS	Last n jobs to visualize
 SCALE-FACTOR	Defaut value is 1.  Each tick in visualization represents 1 minute.
 		If value set to 5, each tick represents 5 minutes  	   
```

## Examples

   $ lein run -m haddash.client localhost 50030 10 1
```bash
job_201603311739_9964  (58 shi): |---------------------------------------------------------->
job_201603311739_9967  (50 shi):         |-------------------------------------------------->
job_201603311739_9983   (3 mes):                                |---|
job_201603311739_9984   (2 mes):                                       |--|
job_201603311739_9985   (2 mes):                                       |--|
job_201603311739_9986   (1 mes):                                        |-|
job_201603311739_9987   (3 mes):                                         |---|
job_201603311739_9988   (8 mes):                                         |--------|
job_201603311739_9989   (2 fit):                                          |--|
job_201603311739_9990  (16 fit):                                            |---------------->
job_201603311739_9991   (1 mes):                                                 |-|
job_201603311739_9992   (2 mes):                                                 |--|
job_201603311739_9993   (1 mes):                                                  |-|
job_201603311739_9994   (3 mes):                                                  |---|
job_201603311739_9995   (8 mes):                                                   |-------->

```

...

### Bugs

Maybe

### TODO:
 - Report job metrics (mappers, reducers, ...)
 - Enhance visualization


## License

Copyright © 2016 Navil Charles

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
