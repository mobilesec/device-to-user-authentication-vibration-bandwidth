####################################################################################
# Analysis of vibration bandwidth user study results (csv files).
# 
# We assume to be in the working directory, e.g.
#     setwd('/media/data/documents/vibration-bandwidth-study/preprocessed')
#
# We expect filenames in the form of "VibrationRecognition_ParticipantId_TryId_Code", e.g. "VibrationRecognition_01_01_33"
# 
# Rainhard Findling
# University of Applied Sciences Upper Austria, Campus Hagenberg
# 07/2015
# rainhard.findling@fh-hagenberg.at 
####################################################################################

library(plyr)

dir.create('plots/')
bg <- 'transparent'
width <- 9
height <- 3.8 # 3 3.8

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
vibration_data <- vibration_data[order(as.character(vibration_data$RealPattern)),]
vibration_data$RealPattern <- as.integer(as.character(vibration_data$RealPattern)) # makes later sorting easier

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
svg(filename = 'plots/barplot_codes_correct_ratio.svg', width = width, height = height, bg=bg)
  with(code_data, barplot(height = correct_ratio, names.arg = code, ylim = c(0,1))) #, col = code))
dev.off()

vibration_data_per_user <- split(vibration_data, f = vibration_data$Participant)
user_data <- data.frame()
for(i in 1:length(vibration_data_per_user)) {
  d <- vibration_data_per_user[[i]]
  d_per_code <- split(d, d$RealPattern)
  user <- names(vibration_data_per_user)[[i]]
  for(i2 in 1:length(d_per_code)) {
    d2 <- d_per_code[[i2]]
    code <- names(d_per_code)[[i2]]
    correct_ratio <- as.numeric(sum(d2$Correct == 'true')) / dim(d2)[[1]]
    user_data <- rbind(user_data, data.frame(user, code, correct_ratio))
  }
}
svg(filename = 'plots/boxplot_codes_correct_ratio.svg', width = width, height = height, bg=bg)
  with(user_data, boxplot(correct_ratio~code, ylim=c(0,1)))
dev.off()
svg(filename = 'plots/boxplot_correctness_per_user.svg', width = width, height = height, bg=bg)
  with(user_data, boxplot(correct_ratio~user, ylim=c(0,1)))
dev.off()

# true positive and true negative rate per user and code
TPR_TNR_per_user <- data.frame()
for(i in 1:length(vibration_data_per_user)) {
  d <- vibration_data_per_user[[i]]
  # TPR
  d_per_code <- split(d, d$RealPattern)
  user <- names(vibration_data_per_user)[[i]]
  for(i2 in 1:length(d_per_code)) {
    d2 <- d_per_code[[i2]]
    code <- as.integer(names(d_per_code)[[i2]])
    correct_ratio <- as.numeric(sum(d2$RealPattern == d2$Pattern & d2$Correct == 'true')) / sum(d2$RealPattern == d2$Pattern)
    TPR_TNR_per_user <- rbind(TPR_TNR_per_user, data.frame(user, code, TPR=correct_ratio, TNR=Inf))
  }
  # TNR
  d_per_code <- split(d, d$Pattern)
  user <- names(vibration_data_per_user)[[i]]
  for(i2 in 1:length(d_per_code)) {
    d2 <- d_per_code[[i2]]
    code <- as.integer(names(d_per_code)[[i2]])
    correct_ratio <- as.numeric(sum(d2$RealPattern != d2$Pattern & d2$Correct == 'true')) / sum(d2$RealPattern != d2$Pattern)
    TPR_TNR_per_user$TNR[TPR_TNR_per_user$code == code & TPR_TNR_per_user$user == user] <- correct_ratio
  }
}
# TPR_TNR_per_user$TPR[is.infinite(TPR_TNR_per_user$TPR)] <- -1
# TPR_TNR_per_user$TNR[is.infinite(TPR_TNR_per_user$TNR)] <- -1
svg(filename = 'plots/boxplot_TPR_all_users_per_code.svg', width = width, height = height, bg=bg)
  with(TPR_TNR_per_user, boxplot(TPR~code, ylim=c(0,1)))
dev.off()
svg(filename = 'plots/boxplot_TNR_all_users_per_code.svg', width = width, height = height, bg=bg)
  with(TPR_TNR_per_user, boxplot(TNR~code, ylim=c(0,1)))
dev.off()

TPR_TNR_per_user2.1 <- TPR_TNR_per_user[,1:3]
names(TPR_TNR_per_user2.1) <- c('user', 'code', 'rate')
TPR_TNR_per_user2.1$type <- 'TPR'
TPR_TNR_per_user2.2 <- TPR_TNR_per_user[,c(1,2,4)]
names(TPR_TNR_per_user2.2) <- c('user', 'code', 'rate')
TPR_TNR_per_user2.2$type <- 'TNR'
TPR_TNR_per_user2 <- rbind(TPR_TNR_per_user2.1, TPR_TNR_per_user2.2)
