// Cloudflare Worker: 转发 API 请求到 Railway 后端
// 部署: Cloudflare Dashboard -> Workers & Pages -> 创建 Worker -> 粘贴此代码
// 部署后获得 worker.dev 域名，配置到前端 .env.production

const BACKEND_URL = 'https://oil-well-cost.up.railway.app';

export default {
  async fetch(request, env, ctx) {
    const url = new URL(request.url);

    // CORS 头
    const corsHeaders = {
      'Access-Control-Allow-Origin': '*',
      'Access-Control-Allow-Methods': 'GET, POST, PUT, DELETE, OPTIONS',
      'Access-Control-Allow-Headers': 'Content-Type, Authorization, token',
    };

    // OPTIONS 预检请求
    if (request.method === 'OPTIONS') {
      return new Response(null, { headers: corsHeaders });
    }

    // 转发请求到后端
    const backendUrl = BACKEND_URL + url.pathname + url.search;
    const headers = new Headers(request.headers);

    try {
      const response = await fetch(backendUrl, {
        method: request.method,
        headers: headers,
        body: request.method !== 'GET' && request.method !== 'HEAD' ? request.body : undefined,
      });

      const responseHeaders = new Headers(response.headers);
      Object.entries(corsHeaders).forEach(([key, value]) => {
        responseHeaders.set(key, value);
      });

      return new Response(response.body, {
        status: response.status,
        headers: responseHeaders,
      });
    } catch (error) {
      return new Response(JSON.stringify({ code: '-1', msg: 'Service unavailable' }), {
        status: 502,
        headers: { 'Content-Type': 'application/json', ...corsHeaders },
      });
    }
  },
};
