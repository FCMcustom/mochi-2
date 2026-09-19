package com.example.ui.lab

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Chemical
import com.example.model.ChemicalState

/**
 * Kệ hóa chất hiển thị danh sách các chất có sẵn trong Virtual Lab.
 * Hỗ trợ cả bố cục LazyRow (điện thoại) và LazyVerticalGrid (máy tính bảng).
 *
 * @param chemicals Danh sách chất hóa học hiển thị
 * @param selectedChemicals Danh sách chất đã được chọn
 * @param onChemicalSelected Callback khi người dùng chọn một chất
 * @param onRemoveChemical Callback khi người dùng xóa một chất đã chọn
 * @param isGridLayout Sử dụng Grid thay vì Row (cho màn hình rộng)
 */
@Composable
fun ChemicalShelf(
    chemicals: List<Chemical>,
    selectedChemicals: List<Chemical>,
    onChemicalSelected: (Chemical) -> Unit,
    onRemoveChemical: (Chemical) -> Unit,
    isGridLayout: Boolean = false,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF0F1E33))
            .border(1.dp, Color(0xFF1B3B60), RoundedCornerShape(16.dp))
            .padding(12.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Science,
                    contentDescription = null,
                    tint = Color(0xFF00E5FF),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "KỆ HÓA CHẤT",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF00E5FF),
                    letterSpacing = 0.5.sp
                )
            }

            // Số chất đã chọn
            if (selectedChemicals.isNotEmpty()) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF00E5FF).copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, Color(0xFF00E5FF).copy(alpha = 0.4f))
                ) {
                    Text(
                        text = "${selectedChemicals.size}/3 chất",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF80D8FF),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Chỉ báo chất đã chọn
        if (selectedChemicals.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                selectedChemicals.forEach { chem ->
                    val isSelectedA = selectedChemicals.indexOf(chem) == 0
                    SelectedChemicalChip(
                        chemical = chem,
                        slotLabel = "Chất ${if (isSelectedA) "1" else "2"}",
                        onRemove = { onRemoveChemical(chem) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        HorizontalDivider(color = Color(0xFF1E3B5F), thickness = 0.5.dp)

        Spacer(modifier = Modifier.height(8.dp))

        // Danh sách hóa chất
        if (isGridLayout) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                modifier = Modifier.heightIn(max = 200.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(chemicals) { chem ->
                    ChemicalPill(
                        chemical = chem,
                        isSelected = selectedChemicals.any { it.id == chem.id },
                        onClick = { onChemicalSelected(chem) }
                    )
                }
            }
        } else {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(chemicals) { chem ->
                    ChemicalPill(
                        chemical = chem,
                        isSelected = selectedChemicals.any { it.id == chem.id },
                        onClick = { onChemicalSelected(chem) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ChemicalPill(
    chemical: Chemical,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val animatedBorderColor by animateColorAsState(
        targetValue = if (isSelected) Color(0xFF00E5FF) else Color(0xFF203B58),
        label = "border_color"
    )
    val animatedBgColor by animateColorAsState(
        targetValue = if (isSelected) Color(0xFF00B0FF).copy(alpha = 0.2f) else Color(0xFF14263D),
        label = "bg_color"
    )

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = animatedBgColor,
        border = BorderStroke(1.dp, animatedBorderColor),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon trạng thái
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(Color(chemical.color), CircleShape)
            )
            Spacer(modifier = Modifier.width(6.dp))

            // Công thức
            Text(
                text = chemical.formula,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) Color(0xFF80D8FF) else Color.White
            )
            Spacer(modifier = Modifier.width(4.dp))

            // Tên ngắn
            Text(
                text = chemical.name,
                style = MaterialTheme.typography.bodySmall,
                fontSize = 10.sp,
                color = Color.LightGray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // Trạng thái
            Spacer(modifier = Modifier.width(4.dp))
            StateIndicator(state = chemical.state)
        }
    }
}

@Composable
private fun StateIndicator(state: ChemicalState) {
    val (icon, color) = when (state) {
        ChemicalState.SOLID -> Icons.Default.GridOn to Color(0xFF90A4AE)
        ChemicalState.LIQUID -> Icons.Default.WaterDrop to Color(0xFF29B6F6)
        ChemicalState.AQUEOUS -> Icons.Default.WaterDrop to Color(0xFF4FC3F7)
        ChemicalState.GAS -> Icons.Default.Air to Color(0xFF81C784)
    }
    Icon(
        imageVector = icon,
        contentDescription = state.label,
        tint = color.copy(alpha = 0.7f),
        modifier = Modifier.size(12.dp)
    )
}

@Composable
private fun SelectedChemicalChip(
    chemical: Chemical,
    slotLabel: String,
    onRemove: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFF142740),
        border = BorderStroke(1.dp, Color(0xFF00E5FF))
    ) {
        Row(
            modifier = Modifier.padding(start = 10.dp, end = 4.dp, top = 6.dp, bottom = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(Color(chemical.color), CircleShape)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Column {
                Text(
                    text = slotLabel,
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 9.sp,
                    color = Color(0xFF00E5FF)
                )
                Text(
                    text = chemical.formula,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            IconButton(
                onClick = onRemove,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Xóa $slotLabel",
                    tint = Color(0xFFFF8A80),
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}
