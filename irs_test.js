import http from 'k6/http';
import { check, sleep } from 'k6';
// $env:K6_WEB_DASHBOARD = "true"
// k6 run .\irs_post.js
// K6_WEB_DASHBOARD=true K6_WEB_DASHBOARD_EXPORT=html-report.html k6 run script.js
export let options = {
  stages: [
    { duration: '30s', target: 50 },   // ramp-up to 50 users
    { duration: '1m', target: 50 },    // hold at 50 users
    { duration: '30s', target: 0 },    // ramp-down
  ],
  thresholds: {
    http_req_duration: ['p(95)<100'], // 95% requests < 1000ms
    http_req_failed: ['rate<0.01'],   // less than 1% failure
  },
};

export default function () {
  let res = http.get('http://localhost:8080/irs/getReportById/240743');
  
  check(res, {
    'status is 200': (r) => r.status === 200,
    'response time < 1000ms': (r) => r.timings.duration < 1000,
  });

  sleep(1); // think time
}
