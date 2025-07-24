OkHttpClient client = new OkHttpClient();

// 查询股价
Request request = new Request.Builder()
  .url("http://localhost:8080/price/TSLA")
  .build();

Response response = client.newCall(request).execute();
System.out.println(response.body().string());

// 买入股票
JSONObject json = new JSONObject();
json.put("username", "Alice");
json.put("stock_id", "TSLA");
json.put("amount", 10);
json.put("payment", 2000.0);

Request post = new Request.Builder()
  .url("http://localhost:8080/buy")
  .post(RequestBody.create(json.toString(), MediaType.get("application/json")))
  .build();

Response postRes = client.newCall(post).execute();
System.out.println(postRes.body().string());