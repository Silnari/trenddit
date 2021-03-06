from reddit_operations import *
from sql_connector import *


def subscribers_ranking(date):
    trenddit_cursor.executemany(
        'INSERT INTO subreddit_ranking (name, subscribers, comments, posts, date) VALUES (%s, %s, %s, %s, %s)',
        [(subreddit.display_name, subreddit.subscribers, 0, 0, date) for subreddit in
         get_subreddits_rank_by_number_of_subscribers()])
    trenddit_db.commit()


def post_ranking(ranking_list, date):
    trenddit_cursor.executemany(
        'UPDATE subreddit_ranking SET posts=posts+%s WHERE name=%s AND date=%s',
        [(subreddit[1], subreddit[0], date) for subreddit in ranking_list])
    trenddit_db.commit()


def comment_ranking(ranking_list, date):
    trenddit_cursor.executemany(
        'UPDATE subreddit_ranking SET comments=comments+%s WHERE name=%s AND date=%s',
        [(subreddit[1], subreddit[0], date) for subreddit in ranking_list])
    trenddit_db.commit()


def get_comment_ranking(date):
    trenddit_cursor.execute('SELECT ROW_NUMBER() OVER (ORDER BY comments DESC) n, name, comments '
                            'FROM subreddit_ranking WHERE date=%s ORDER BY comments DESC', (date,))
    return trenddit_cursor.fetchall()


def update_subscriber_ranking(date):
    subscribers_ranking(date)


def update_posts(date):
    post_ranking(get_subreddits_rank_by_number_of_last_hour_posts(), date)


def update_comments(date):
    comment_ranking(get_subreddits_rank_by_number_of_last_day_comments(), date)
