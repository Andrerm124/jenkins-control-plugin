/*
 * Copyright (c) 2013 David Boissier
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.codinjutsu.tools.jenkins.model;

import com.intellij.icons.AllIcons;
import icons.JenkinsControlIcons;
import org.codinjutsu.tools.jenkins.util.DateUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.EnumMap;
import java.util.Map;

public class Build {

    public static final Build NULL = new Build();

    public static final Map<BuildStatusEnum, Icon> ICON_BY_BUILD_STATUS_MAP = new EnumMap<>(BuildStatusEnum.class);

    private String url;
    private Date buildDate;
    private int number;
    private boolean building;
    private String message;
    private Date timestamp;
    private Long duration;

    private BuildStatusEnum status;

    static {
        ICON_BY_BUILD_STATUS_MAP.put(BuildStatusEnum.SUCCESS, JenkinsControlIcons.Job.BLUE);
        ICON_BY_BUILD_STATUS_MAP.put(BuildStatusEnum.STABLE, JenkinsControlIcons.Job.BLUE);
        ICON_BY_BUILD_STATUS_MAP.put(BuildStatusEnum.FAILURE, JenkinsControlIcons.Job.RED);
        ICON_BY_BUILD_STATUS_MAP.put(BuildStatusEnum.UNSTABLE, JenkinsControlIcons.Job.YELLOW);
        ICON_BY_BUILD_STATUS_MAP.put(BuildStatusEnum.NULL, JenkinsControlIcons.Job.GREY);
        ICON_BY_BUILD_STATUS_MAP.put(BuildStatusEnum.ABORTED, JenkinsControlIcons.Job.GREY);
        ICON_BY_BUILD_STATUS_MAP.put(BuildStatusEnum.FOLDER, AllIcons.Nodes.Folder);
    }

    public static Build createBuildFromWorkspace(String buildUrl, String number, String status, boolean isBuilding, String buildDate, Long timestamp, Long duration) {
        return createBuild(buildUrl, Long.parseLong(number), status, isBuilding, buildDate, DateUtil.WORKSPACE_DATE_FORMAT, null, timestamp, duration);
    }

    public static Build createBuildFromRss(String buildUrl, String number, String status, boolean isBuilding, String buildDate, String message) {
        return createBuild(buildUrl, Long.parseLong(number), status, isBuilding, buildDate, DateUtil.RSS_DATE_FORMAT, message, 0L, 0L);
    }

    private static Build createBuild(String buildUrl, Long number, String status, boolean isBuilding, String buildDate, SimpleDateFormat simpleDateFormat, String message, Long timestamp, Long duration) {
        BuildStatusEnum buildStatusEnum = BuildStatusEnum.parseStatus(status);
        Date date = DateUtil.parseDate(buildDate, simpleDateFormat);

        return new Build(buildUrl, number.intValue(), date, buildStatusEnum, isBuilding, message, timestamp, duration);
    }

    public Build() {
    }

    private Build(String url, int number, Date buildDate, BuildStatusEnum status, boolean isBuilding, String message, Long timestamp, Long duration) {
        this.url = url;
        this.number = number;
        this.buildDate = buildDate;
        this.status = status;
        this.building = isBuilding;
        this.message = message;
        setTimestamp(timestamp);
        this.duration = duration;
    }

    @NotNull
    public static Icon getStateIcon(String jobColor) {
        if (jobColor == null) {
            // NB: This assumes the case of rendering a folder.
            // TODO: handle the folder-case explicitly
            return ICON_BY_BUILD_STATUS_MAP.get(BuildStatusEnum.FOLDER);
        }
        BuildStatusEnum[] jobStates = BuildStatusEnum.values();
        for (BuildStatusEnum jobStatus : jobStates) {
            if (jobStatus.getColor().isForJobColor(jobColor)) {
                return ICON_BY_BUILD_STATUS_MAP.get(jobStatus);
            }
        }

        return ICON_BY_BUILD_STATUS_MAP.get(BuildStatusEnum.NULL);
    }

    public Icon getStateIcon() {
        return ICON_BY_BUILD_STATUS_MAP.get(status);
    }


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public BuildStatusEnum getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = BuildStatusEnum.parseStatus(status);
    }

    @NotNull
    public Date getBuildDate() {
        if (buildDate == null) {
            buildDate = getTimestamp();
        }
        return buildDate;
    }

    public void setBuildDate(Date buildDate) {
        this.buildDate = buildDate;
    }

    @NotNull
    public Date getTimestamp() {
        if (timestamp == null) {
            timestamp = new Date();
        }
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = new Date(timestamp);
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public boolean isBuilding() {
        return building;
    }

    public void setBuilding(boolean building) {
        this.building = building;
    }

    public boolean isAfter(Build aBuild) {
        return this.getNumber() > aBuild.getNumber();
    }

    public String getMessage() {
        return message;
    }
}
