
from math import *

# a_1 - acceleration
# a_2 - deceleration
# x_2 - target
# returns   t_1 - time of acceleration
#           t_2 - time of deceleration 
def acde_celeration(x_0, v_0, a_1, a_2, x_2):
    x_0 = float(x_0)
    v_0 = float(v_0)
    a_1 = float(a_1)
    a_2 = float(a_2)
    x_2 = float(x_2)
    
    a = (a_1 + a_1**2 / a_2) / 2.
    b = v_0 * (1 + a_1/a_2)
    c = x_0 - x_2 + v_0**2 / (2. * a_2) 
    
    D = b*b - 4*a*c
    if D < 0: raise Exception("discriminant less than zero")
    
    t_1_plus = (-b + sqrt(D)) / (2. * a)
    t_1_minus = (-b - sqrt(D)) / (2. * a)
    
    if t_1_minus < 0: t_1 = t_1_plus
    if t_1_plus < 0: t_1 = t_1_minus
    
    if t_1_minus > 0 and t_1_plus > 0: raise Exception("multiroot equation")
    if t_1_minus < 0 and t_1_plus < 0: raise Exception("no root exception")
    
    t_2 = (v_0 + a_1 * t_1) / a_2
    
    return t_1, t_2
    
def stop_time(v, a):
    return v/a
    
def distance(v, a, t):
    return v*t + a*t*t/2.