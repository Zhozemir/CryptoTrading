CREATE TABLE IF NOT EXISTS holdings (
  id INT AUTO_INCREMENT PRIMARY KEY,
  crypto VARCHAR(50) NOT NULL,
  quantity DECIMAL(20,8) NOT NULL,
  average_cost DECIMAL(20,8) NOT NULL
);

CREATE TABLE IF NOT EXISTS transactions (
  id INT AUTO_INCREMENT PRIMARY KEY,
  type VARCHAR(10) NOT NULL,
  crypto VARCHAR(50) NOT NULL,
  quantity DECIMAL(20,8) NOT NULL,
  price DECIMAL(20,8) NOT NULL,        -- при SELL
  timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
