####################################################################################
# Analysis of vibration bandwidth user study results (csv files).
# 
# We assume to be in the working directory, e.g.
#     setwd('/media/data/documents/vibration-bandwidth-study/2015_07_16_muhammad/data')
# 
# Rainhard Findling
# University of Applied Sciences Upper Austria, Campus Hagenberg
# 07/2015
# rainhard.findling@fh-hagenberg.at 
####################################################################################

library(plyr)

dir.create('plots/')

files <- dir('.', pattern='.csv')
vibration_data <- lapply(files, FUN = function(f) {
  metadata <- unlist(strsplit(x = gsub(pattern = '.csv', replacement = '', x = f), split = '_'))[-c(1)]
  names(metadata) <- c('Participant', 'Try', 'RealPattern')
  metadata[[1]] <- gsub(pattern = 'participant', replacement = '', x = metadata[[1]])
  metadata[[2]] <- gsub(pattern = 'try', replacement = '', x = metadata[[2]])
  d <- read.csv(f)
  cbind(d, t(metadata))
})
vibration_data <- ldply(vibration_data)

vibration_data_per_code <- split(vibration_data, f = vibration_data$RealPattern)
# code_data <- lapply(vibration_data_per_code, FUN = function(d) {
code_data <- data.frame()
for(i in 1:length(vibration_data_per_code)) {
  d <- vibration_data_per_code[[i]]
  code <- names(vibration_data_per_code)[[i]]
  correct_ratio <- as.numeric(sum(d$Correct == 'true')) / dim(d)[[1]]
  code_data <- rbind(code_data, data.frame(code, correct_ratio))
}

# barplot of code correctnes over all users
bg = 'transparent'
svg(filename = 'plots/barplot_codes_correct_ratio.svg', width = 8, height = 4, bg=bg)
  with(code_data, barplot(height = correct_ratio, names.arg = code, ylim = c(0,1))) #, col = code))
dev.off()

vibration_data_per_user <- split(vibration_data, f = vibration_data$Participant)
user_data <- data.frame()
for(i in 1:length(vibration_data_per_user)) {
  d <- vibration_data_per_user[[i]]
  d_per_code <- split(d, d$RealPattern)
  user <- names(vibration_data_per_user)[[i]]
  for(i2 in 1:(dim(d_per_code)[[1]])) {
    d2 <- d_per_code[[i2]]
    code <- names(d_per_code)[[i]]
    correct_ratio <- as.numeric(sum(d2$Correct == 'true')) / dim(d2)[[1]]
    user_data <- rbind(user_data, data.frame(user, code, correct_ratio))
  }
  correct_ratio <- as.numeric(sum(d$Correct == 'true')) / dim(d)[[1]]
  code_data <- rbind(code_data, data.frame(code, correct_ratio))
}

