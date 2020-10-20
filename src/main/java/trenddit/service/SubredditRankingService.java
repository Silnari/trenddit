package trenddit.service;

import org.springframework.stereotype.Service;
import trenddit.bean.SubredditMetric;
import trenddit.bean.SubredditRankedMetric;
import trenddit.dao.SubredditRankingRepository;
import trenddit.entity.SubredditRanking;

import javax.persistence.Tuple;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SubredditRankingService {

    private final SubredditRankingRepository subredditRankingRepository;

    public SubredditRankingService(SubredditRankingRepository subredditRankingRepository) {
        this.subredditRankingRepository = subredditRankingRepository;
    }

    public List<SubredditMetric> getSubscriberRanking() {
        return subredditRankingRepository.getByDateOrderBySubscribersDesc(new Date())
                .stream().map(subreddit -> new SubredditMetric(subreddit.getName(), subreddit.getSubscribers()))
                .collect(Collectors.toList());
    }

    public List<SubredditRanking> getTodayMostCommented() {
        return subredditRankingRepository.findTop10ByDateOrderByCommentsDesc(new Date());
    }

    public List<SubredditMetric> getAverageComments(Integer days) {
        return mapToSubredditMetric(subredditRankingRepository.findAverageComments(
                daysAgo(0), daysAgo(days)), "comments");
    }

    public List<SubredditRanking> getTodayMostPosted() {
        return subredditRankingRepository.findTop10ByDateOrderByPostsDesc(new Date());
    }

    public List<SubredditMetric> getAveragePosted(Integer days) {
        return mapToSubredditMetric(subredditRankingRepository.findAveragePosts(
                daysAgo(0), daysAgo(days)), "posts");
    }

    public List<SubredditMetric> getSubredditsGrowth(Integer days, Integer limit) {
        return mapToSubredditMetric(subredditRankingRepository.findSubredditsGrowth(
                daysAgo(0), daysAgo(days), days, limit == null ? 9999 : limit), "growth");
    }

    public SubredditMetric getSubredditGrowth(String subreddit, Integer days) {
        return mapToSubredditMetric(subredditRankingRepository.findSubredditGrowth(
                subreddit, daysAgo(0), daysAgo(days), days), "growth");
    }

    public SubredditRankedMetric getSubredditRankedList(String subredditName, String metric) {
        List<SubredditMetric> subredditsGrowth = getMetricList(metric, 1);
        if (subredditsGrowth == null) return null;
        final int[] i = {1};
        List<SubredditRankedMetric> subredditsGrowthRanked = subredditsGrowth.stream()
                .map(subreddit -> new SubredditRankedMetric(
                        subreddit.getName(),
                        subreddit.getNumber(),
                        i[0]++)).collect(Collectors.toList());
        return subredditsGrowthRanked.stream()
                .filter(subreddit -> subreddit.getName().equals(subredditName))
                .findAny().orElse(null);
    }

    public List<SubredditRanking> getSubredditMetricGrowthOverTime(String subredditName) {
        return subredditRankingRepository.findAllByName(subredditName);
    }

    private List<SubredditMetric> getMetricList(String metric, Integer days) {
        switch (metric) {
            case "growth":
                return getSubredditsGrowth(days, 9999);
            case "comments":
                return getAverageComments(days);
            case "posts":
                return getAveragePosted(days);
            case "subscribers":
                return getSubscriberRanking();
            default:
                return null;
        }
    }

    private SubredditMetric mapToSubredditMetric(Tuple tuple, String metricName) {
        return new SubredditMetric(
                (String) tuple.get("name"),
                tuple.get(metricName) == null ? 0 : ((BigDecimal) tuple.get(metricName)).intValue());
    }

    private List<SubredditMetric> mapToSubredditMetric(List<Tuple> tupleList, String metricName) {
        return tupleList.stream().map(tuple -> mapToSubredditMetric(tuple, metricName)).collect(Collectors.toList());
    }

    private String daysAgo(Integer days) {
        return dateToString(ago(days));
    }

    private Date ago(Integer days) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -days);
        return cal.getTime();
    }

    private String dateToString(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }

}
