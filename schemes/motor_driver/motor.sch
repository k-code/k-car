v 20110115 2
C 40000 40000 0 0 0 title-B.sym
C 41200 46100 1 180 0 connector2-2.sym
{
T 40500 44800 5 10 1 1 180 6 1
refdes=CONN2
T 40900 44850 5 10 0 0 180 0 1
device=CONNECTOR_2
T 40900 44650 5 10 0 0 180 0 1
footprint=SIP2
}
C 41700 46900 1 0 0 resistor-2.sym
{
T 42100 47250 5 10 0 0 0 0 1
device=RESISTOR
T 41900 47200 5 10 1 1 0 0 1
refdes=R3
T 41700 46900 5 10 0 0 0 0 1
footprint=R025
}
C 42300 45200 1 0 0 resistor-2.sym
{
T 42700 45550 5 10 0 0 0 0 1
device=RESISTOR
T 42500 45500 5 10 1 1 0 0 1
refdes=R4
T 42300 45200 5 10 0 0 0 0 1
footprint=R025
}
C 41200 44200 1 180 0 connector2-2.sym
{
T 40500 42900 5 10 1 1 180 6 1
refdes=CONN3
T 40900 42950 5 10 0 0 180 0 1
device=CONNECTOR_2
T 40900 42750 5 10 0 0 180 0 1
footprint=SIP2
}
C 45100 44400 1 0 0 gnd-1.sym
C 47400 44400 1 0 0 gnd-1.sym
C 42800 49400 1 0 0 gnd-1.sym
C 44800 49100 1 0 0 resistor-2.sym
{
T 45200 49450 5 10 0 0 0 0 1
device=RESISTOR
T 45000 49400 5 10 1 1 0 0 1
refdes=R2
T 44800 49100 5 10 0 0 0 0 1
footprint=R025
}
C 47300 49600 1 0 0 resistor-2.sym
{
T 47700 49950 5 10 0 0 0 0 1
device=RESISTOR
T 47500 49900 5 10 1 1 0 0 1
refdes=R1
T 47300 49600 5 10 0 0 0 0 1
footprint=R025
}
N 41200 45700 41700 45700 4
N 41700 45700 41700 47000 4
N 44200 46500 44200 45900 4
N 44200 45900 44600 45900 4
N 45200 45400 45200 44700 4
N 47500 44700 47500 45400 4
N 45200 47600 45200 46400 4
N 47500 47600 47500 46400 4
N 48700 46500 48700 45900 4
N 48700 45900 48100 45900 4
N 44200 47500 44200 49200 4
N 44200 49200 44800 49200 4
N 45700 49200 48100 49200 4
N 48100 49200 48100 48100 4
N 47300 49700 44600 49700 4
N 44600 49700 44600 48100 4
N 48200 49700 48700 49700 4
N 48700 49700 48700 47500 4
N 45200 48600 45200 50400 4
N 41400 50400 47500 50400 4
N 47500 48600 47500 50400 4
N 41400 50000 42900 50000 4
N 42900 50000 42900 49700 4
N 41200 43800 46000 43800 4
N 47500 47000 46700 47000 4
N 46700 47000 46700 43400 4
N 46700 43400 41200 43400 4
N 46000 43800 46000 47000 4
N 46000 47000 45200 47000 4
N 41200 45300 42300 45300 4
N 43200 45300 49300 45300 4
N 49300 45300 49300 47000 4
C 43600 46500 1 0 0 C9014.sym
{
T 44500 47000 5 10 0 0 0 0 1
device=NPN_TRANSISTOR
T 44500 47000 5 10 1 1 0 0 1
refdes=Q3
T 44500 46800 5 10 0 0 0 0 1
footpring=TO92
}
C 49300 46500 1 0 1 C9014.sym
{
T 48400 47000 5 10 0 0 0 6 1
device=NPN_TRANSISTOR
T 48400 47000 5 10 1 1 0 6 1
refdes=Q4
T 48400 46800 5 10 0 0 0 6 1
footpring=TO92
}
N 42600 47000 43600 47000 4
C 48100 48600 1 180 0 S8550.sym
{
T 47200 48100 5 10 0 0 180 0 1
device=PNP_TRANSISTOR
T 47200 48100 5 10 1 1 180 0 1
refdes=Q2
T 48105 48605 5 10 0 1 180 0 1
footprint=TO92
}
C 44600 48600 1 180 1 S8550.sym
{
T 45500 48100 5 10 0 0 180 6 1
device=PNP_TRANSISTOR
T 45500 48100 5 10 1 1 180 6 1
refdes=Q1
T 44595 48605 5 10 0 0 180 6 1
footprint=TO92
}
C 44600 45400 1 0 0 2SC1213.sym
{
T 45500 45900 5 10 0 0 0 0 1
device=NPN_TRANSISTOR
T 45500 45900 5 10 1 1 0 0 1
refdes=Q5
T 44600 45400 5 10 0 0 0 0 1
footprint=TO92
}
C 48100 45400 1 0 1 2SC1213.sym
{
T 47200 45900 5 10 0 0 0 6 1
device=NPN_TRANSISTOR
T 47200 45900 5 10 1 1 0 6 1
refdes=Q6
T 48100 45400 5 10 0 1 0 0 1
footprint=TO92
}
C 41400 50800 1 180 0 connector2-2.sym
{
T 40700 49500 5 10 1 1 180 6 1
refdes=CONN1
T 41100 49550 5 10 0 0 180 0 1
device=CONNECTOR_2
T 41100 49350 5 10 0 0 180 0 1
footprint=SIP2
}
