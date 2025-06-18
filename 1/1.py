#區塊一
import torch
from torchvision import models, transforms
print(torch.__version__)
#區塊二
import os
import numpy as np
import torch
import torch.nn as nn
import torch.optim as optim
from torch.utils.data import TensorDataset, DataLoader
from torchvision import models, transforms
from sklearn.metrics import confusion_matrix
import seaborn as sns
import matplotlib.pyplot as plt
#區塊三
from torchvision import datasets, transforms
from torch.utils.data import DataLoader

data_root = 'data'
batch_size = 32

# 定義轉換
transform = transforms.Compose([
    transforms.Resize((224, 224)),  # ResNet 輸入需要 224x224
    transforms.ToTensor(),
    transforms.Normalize(mean=[0.485, 0.456, 0.406],  # ResNet預訓練模型的平均值
                         std=[0.229, 0.224, 0.225])
])

# 使用 ImageFolder 直接讀資料夾
train_dataset = datasets.ImageFolder(os.path.join(data_root, 'train'), transform=transform)
val_dataset = datasets.ImageFolder(os.path.join(data_root, 'val'), transform=transform)
test_dataset = datasets.ImageFolder(os.path.join(data_root, 'test'), transform=transform)

# 建立 DataLoader
train_loader = DataLoader(train_dataset, batch_size=batch_size, shuffle=True)
val_loader = DataLoader(val_dataset, batch_size=batch_size)
test_loader = DataLoader(test_dataset, batch_size=batch_size)

#區塊四
from PIL import Image
import numpy as np
import os

# 類別名稱到數字標籤的對應（依你資料夾結構調整）
class_names = [
    'Bacterial leaf blight of rice',
    'Bakanae disease',
    'Brown spot',
    'healthy',
    'Rhizoctonia blight',
    'Rice blast',
    'Sheath blight',
    'white-tup nematode'
]
class_to_idx = {name: i for i, name in enumerate(class_names)}

def load_dataset(base_path, augment=False):
    imgs = []
    labels = []
    print(f"🚀 載入資料集：{base_path}")
    for class_name in os.listdir(base_path):
        class_path = os.path.join(base_path, class_name)
        print(f"🔍 處理類別：{class_name}")
        if not os.path.isdir(class_path):
            continue
        label = class_to_idx.get(class_name)
        if label is None:
            print(f"⚠️ 類別 '{class_name}' 沒有對應標籤，略過")
            continue
        for file in os.listdir(class_path):
            path = os.path.join(class_path, file)
            try:
                img = Image.open(path).convert('RGB')
                img = img.resize((224, 224))
                img_array = np.array(img)
                imgs.append(img_array)
                labels.append(label)
            except Exception as e:
                print(f"❌ 圖片讀取失敗：{path}，錯誤：{e}")
    print(f"✅ 載入完成，共 {len(imgs)} 張圖片")
    return np.array(imgs), np.array(labels)


#區塊五
train_img, train_label = load_dataset('data/train', augment=True)
val_img, val_label = load_dataset('data/val')
test_img, test_label = load_dataset('data/test')

#區塊六
# 裝置設定
device = torch.device("cuda" if torch.cuda.is_available() else "cpu")


#區塊七
# 圖像維度檢查
print("train_img shape:", train_img.shape)
print("val_img shape:", val_img.shape)
print("test_img shape:", test_img.shape)
if train_img.ndim != 4 or val_img.ndim != 4 or test_img.ndim != 4:
    raise ValueError("圖像資料格式錯誤，期望為(N, 224, 224, 3)")

#區塊八
#  圖⽚轉為PyTorch tensor 並轉換維度，並正規化⾄[0, 1]
train_img = torch.tensor(train_img / 255.0, dtype=torch.float32).permute(0, 3, 1, 2)
val_img = torch.tensor(val_img / 255.0, dtype=torch.float32).permute(0, 3, 1, 2)
test_img = torch.tensor(test_img / 255.0, dtype=torch.float32).permute(0, 3, 1, 2)
train_label = torch.tensor(train_label, dtype=torch.long)
val_label = torch.tensor(val_label, dtype=torch.long)
test_label = torch.tensor(test_label, dtype=torch.long)

#區塊九
train_img_tensor = torch.tensor(train_img / 255.0, dtype=torch.float32).permute(0, 3, 1, 2)
val_img_tensor = torch.tensor(val_img / 255.0, dtype=torch.float32).permute(0, 3, 1, 2)
test_img_tensor = torch.tensor(test_img / 255.0, dtype=torch.float32).permute(0, 3, 1, 2)
print("train_img_tensor shape:", train_img_tensor.shape)
print("val_img_tensor shape:", val_img_tensor.shape)
print("test_img_tensor shape:", test_img_tensor.shape)
train_loader = DataLoader(TensorDataset(train_img_tensor, train_label), batch_size=batch_size, shuffle=True)
val_loader = DataLoader(TensorDataset(val_img_tensor, val_label), batch_size=batch_size)
test_loader = DataLoader(TensorDataset(test_img_tensor, test_label), batch_size=batch_size)


#區塊十
import os
import torch
import torch.nn as nn
import torch.optim as optim
from torch.utils.data import DataLoader
from torchvision import datasets, transforms, models
from torch.optim.lr_scheduler import ReduceLROnPlateau

# 檢查設備
device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
print("使用設備:", device)

# 設定資料目錄與超參數
data_root = 'data'
batch_size = 32
num_epochs = 20
num_classes = 8  # 分成 8 類

# 定義資料轉換
transform = transforms.Compose([
    transforms.Resize((224, 224)),
    transforms.ToTensor(),
    transforms.Normalize([0.485, 0.456, 0.406],  # ImageNet 的 mean
                         [0.229, 0.224, 0.225])  # ImageNet 的 std
])

# 載入資料集
train_dataset = datasets.ImageFolder(os.path.join(data_root, 'train'), transform=transform)
val_dataset = datasets.ImageFolder(os.path.join(data_root, 'val'), transform=transform)
test_dataset = datasets.ImageFolder(os.path.join(data_root, 'test'), transform=transform)

train_loader = DataLoader(train_dataset, batch_size=batch_size, shuffle=True)
val_loader = DataLoader(val_dataset, batch_size=batch_size)
test_loader = DataLoader(test_dataset, batch_size=batch_size)

# 建立模型
model = models.resnet50(weights=models.ResNet50_Weights.DEFAULT)
model.fc = nn.Linear(model.fc.in_features, num_classes)
model = model.to(device)

# 損失函數與優化器
criterion = nn.CrossEntropyLoss()
optimizer = optim.Adam(model.parameters(), lr=1e-4)
scheduler = ReduceLROnPlateau(optimizer, mode='min', factor=0.5, patience=2)

# 訓練模型
best_acc = 0.0
train_losses, val_losses = [], []
train_accuracies, val_accuracies = [], []

for epoch in range(num_epochs):
    model.train()
    train_loss, correct, total = 0.0, 0, 0
    for images, labels in train_loader:
        images, labels = images.to(device), labels.to(device)
        optimizer.zero_grad()
        outputs = model(images)
        loss = criterion(outputs, labels)
        loss.backward()
        optimizer.step()

        train_loss += loss.item()
        _, predicted = torch.max(outputs, 1)
        total += labels.size(0)
        correct += (predicted == labels).sum().item()

    train_acc = 100 * correct / total
    avg_train_loss = train_loss / len(train_loader)

    # 驗證
    model.eval()
    val_loss, val_correct, val_total = 0.0, 0, 0
    with torch.no_grad():
        for images, labels in val_loader:
            images, labels = images.to(device), labels.to(device)
            outputs = model(images)
            loss = criterion(outputs, labels)
            val_loss += loss.item()
            _, predicted = torch.max(outputs, 1)
            val_total += labels.size(0)
            val_correct += (predicted == labels).sum().item()

    val_acc = 100 * val_correct / val_total
    avg_val_loss = val_loss / len(val_loader)

    train_losses.append(avg_train_loss)
    val_losses.append(avg_val_loss)
    train_accuracies.append(train_acc)
    val_accuracies.append(val_acc)

    print(f"Epoch [{epoch+1}/{num_epochs}] "
          f"Train Loss: {avg_train_loss:.4f}, Train Acc: {train_acc:.2f}%, "
          f"Val Loss: {avg_val_loss:.4f}, Val Acc: {val_acc:.2f}%")

    scheduler.step(avg_val_loss)

    # 儲存最佳模型
    if val_acc > best_acc:
        best_acc = val_acc
        torch.save(model.state_dict(), "best_resnet50.pth")
        print("🔽 儲存最佳模型 (val acc: {:.2f}%)".format(val_acc))
