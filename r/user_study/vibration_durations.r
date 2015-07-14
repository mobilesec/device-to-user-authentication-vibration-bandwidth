vibration_duration <- 60
pause_short <- 60
pause_group <- 200

vibrations <- data.frame(t(data.frame(c(1,0), 
                                      c(1,1),
                                      c(1,2),
                                      c(1,3),
                                      c(2,0),
                                      c(2,1),
                                      c(2,2),
                                      c(2,3),
                                      c(3,0),
                                      c(3,1),
                                      c(3,2),
                                      c(3,3))))
names(vibrations) <- c('group1', 'group2')
row.names(vibrations) <- NULL
vibrations$duration <- with(vibrations, group1 * vibration_duration + (group1 - 1) * pause_short)
vibrations$duration[vibrations$group2 > 0] <- with(vibrations[vibrations$group2 > 0,], duration + pause_group + group2 * vibration_duration + (group2 - 1) * pause_short)
# distribution of pattern durations
with(vibrations, hist(duration, breaks = 8))
# average bandwidth in bit per second
pattern_entropy <- log(dim(vibrations)[[1]])/log(2)
bps <- pattern_entropy / with(vibrations, mean(duration)) * 1000

