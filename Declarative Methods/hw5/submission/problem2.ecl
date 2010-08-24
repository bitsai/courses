:- lib(branch_and_bound).
:- lib(ic).

% Answer to 2a
contains([A|B], A).
contains([A|B], C) :- contains(B, C).

inc_subseq([A|B], [A]).
inc_subseq([A|B], [C]) :- contains(B, C).
inc_subseq([A|B], [A,X|Y]) :- A #< X, inc_subseq(B, [X|Y]).
inc_subseq([A|B], [X,Y|Z]) :- inc_subseq(B, [X,Y|Z]).

% Answer to 2b
inc_subseq_3(A, B) :- inc_subseq(A, B), length(B, 3).
find_3(List, Count) :- findall(S, inc_subseq_3(List, S), Ss), length(Ss, Count).

% Answer to 2d
find_longest(List, Longest) :- findall(S, inc_subseq(List, S), Ss), minimize((member(Longest, Ss), length(Longest, Length), Cost is -Length), Cost).
