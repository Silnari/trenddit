async function drawGrowthChart(subredditMetricGrowth) {
    let dates = []
    let subscribers = []
    let comments = []
    let posts = []

    subredditMetricGrowth.forEach(subreddit => {
        dates.push(subreddit.date)
        subscribers.push(subreddit.subscribers)
        comments.push(subreddit.comments)
        posts.push(subreddit.posts)
    })

    drawChart('growth-subscribers', dates, subscribers, 'Subscribers')
    drawChart('growth-comments', dates, comments, 'Comments')
    drawChart('growth-posts', dates, posts, 'Posts')
    drawChart('comments-to-posts', dates,
        comments.map((c, i) => Number(posts[i] !== 0 ? c / posts[i] : c).toFixed(2)), 'Comment / Posts')
}

function drawChart(canvasId, dates, values, label) {
    new Chart(document.getElementById(canvasId).getContext("2d"), {
        type: 'line',
        data: {
            labels: dates,
            datasets: [{
                label: label,
                data: values,
                backgroundColor: 'rgba(0, 0, 0, 0)',
                borderColor: 'rgba(0, 123, 255, 1)',
                borderWidth: 3
            }]
        },
        options: {
            scales: {
                yAxes: [{
                    ticks: {
                        beginAtZero: false
                    }
                }],
                xAxes: [{
                    afterTickToLabelConversion: data => {
                        const xLabels = data.ticks;
                        xLabels.forEach((labels, i) => {
                            if (i % 3 !== 0) {
                                xLabels[i] = '';
                            }
                        });
                    }
                }]
            }
        }
    })
}