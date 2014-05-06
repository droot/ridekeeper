
RideKeeper
==========

A play based application which exposes following endpoints:
  * /v1.0/rides --> POST endpoint to create a new ride 
  * /v1.0/rides/count?t1=<start_time>&t2=<end_time> --> GET endpoint to
    retrieve total number of rides in given range
  * /v1.0/clients/count?t1=<start_time>&t2=<end_time> --> GET enpoint to
    retrieve total number of clients in given time range
  * /v1.0/fares/avg?latMin=<>&lonMin=<>&latMax=<>&lonMax=<>&t1=<>&t2<> --
    GET endpoint which gives avg fare within bounds and time range 


Project uses the following
  * MySQL as storage for rides data
  * Caching Layer to store the aggregates. Currently cache invalidates all the
    keys when a new ride is added. This is a bit inefficient but can be
    improved with more granular invalidation
