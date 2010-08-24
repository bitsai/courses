duplicate([], []).
duplicate([X|Y], [X,X|Z]) :- duplicate(Y, Z).
