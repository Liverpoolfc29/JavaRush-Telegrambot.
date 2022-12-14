package com.github.liverpoolfc29.jrtb.service;

import com.github.liverpoolfc29.jrtb.dto.GroupStatDTO;
import com.github.liverpoolfc29.jrtb.dto.StatisticDTO;
import com.github.liverpoolfc29.jrtb.repository.entity.TelegramUser;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.util.CollectionUtils.isEmpty;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    private final GroupSubService groupSubService;
    private final TelegramUserService telegramUserService;

    public StatisticsServiceImpl(GroupSubService groupSubService, TelegramUserService telegramUserService) {
        this.groupSubService = groupSubService;
        this.telegramUserService = telegramUserService;
    }

    @Override
    public StatisticDTO countBotStatistic() {
        List<GroupStatDTO> groupStatDTOList = groupSubService.findAll().stream()
                .filter(it -> !isEmpty(it.getTelegramUsers()))
                .map(groupSub -> new GroupStatDTO(groupSub.getId(), groupSub.getTitle(), groupSub.getTelegramUsers().size()))
                .collect(Collectors.toList());
        List<TelegramUser> allInActiveUsers = telegramUserService.findAllInActiveUsers();
        List<TelegramUser> allActiveUsers = telegramUserService.findAllActiveUsers();

        double groupsPerUser = getGroupsPerUser(allActiveUsers);
        return new StatisticDTO(allActiveUsers.size(), allInActiveUsers.size(), groupStatDTOList, groupsPerUser);
    }

    private double getGroupsPerUser(List<TelegramUser> allActiveUsers) {
        return (double) allActiveUsers.stream().mapToInt(it -> it.getGroupSubs().size()).sum() / allActiveUsers.size();
    }

}