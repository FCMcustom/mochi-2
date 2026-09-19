import express from 'express';
import path from 'path';
import { GoogleGenAI } from '@google/genai';

async function startServer() {
  const app = express();
  const PORT = 3000;

  app.use(express.json());

  // Helper for Gemini AI client (lazy initialization)
  function getGeminiClient(): GoogleGenAI | null {
    const key = process.env.GEMINI_API_KEY;
    if (key && key !== 'MY_GEMINI_API_KEY' && key.trim().length > 0) {
      return new GoogleGenAI({ apiKey: key });
    }
    return null;
  }

  // Health check endpoint
  app.get('/api/health', (req, res) => {
    res.json({ status: 'ok', timestamp: Date.now() });
  });

  // Socratic consult endpoint
  app.post('/api/socratic/consult', async (req, res) => {
    const { userMessage, experimentContext, studentHypothesis } = req.body;
    const ai = getGeminiClient();

    if (ai) {
      try {
        const prompt = `
Bạn là 'Trợ lý phòng Lab Hóa học Socratic' theo chuẩn chương trình GDPT 2018 của Bộ Giáo dục và Đào tạo Việt Nam.
Nguyên tắc phản hồi Socratic:
- TUYỆT ĐỐI KHÔNG đưa ra ngay câu trả lời trực tiếp nếu học sinh hỏi 'chất nào phản ứng' hay 'kết quả ra sao'.
- Hãy đặt câu hỏi gợi mở định hướng vào: Dãy hoạt động hóa học/thế điện cực chuẩn, điều kiện trao đổi ion (kết tủa, bay hơi), hoặc quá trình chuyển giao electron (vi mô).
- Bám sát danh pháp IUPAC kết hợp tên tiếng Việt SGK 2018 (Ví dụ: Zinc/Kẽm, Hydrochloric acid/Axit clohidric).
- Ngắn gọn, thân thiện, mang tính kích thích tư duy khoa học (khoảng 80-120 từ).

Ngữ cảnh thí nghiệm hiện tại: ${experimentContext || 'Chưa chọn chất'}
Giả thuyết học sinh đã chọn: ${studentHypothesis || 'Chưa đưa ra giả thuyết'}
Học sinh hỏi/nói: "${userMessage}"
`.trim();

        const response = await ai.models.generateContent({
          model: 'gemini-2.5-flash',
          contents: prompt
        });

        if (response.text) {
          return res.json({ text: response.text });
        }
      } catch (err: any) {
        console.warn('Gemini Socratic consult API error, using local fallback:', err?.message || err);
      }
    }

    // Local Socratic Fallback
    const lower = (userMessage || '').toLowerCase();
    let reply = "💡 **Trợ lý Socratic:** Câu hỏi rất hay! Trước khi thầy đưa ra kết luận, em hãy quan sát kỹ hai hiện tượng: (1) Màu sắc dung dịch và bọt khí (vĩ mô), (2) Sự trao đổi electron giữa các hạt phân tử (vi mô). Em dự đoán liên kết nào vừa bị bẻ gãy?";

    if (lower.includes('cu') || lower.includes('đồng') || lower.includes('không phản ứng')) {
      reply = "💡 **Gợi ý Socratic:** Em hãy nhớ lại vị trí của Đồng (Cu) và Hydro (H) trong dãy hoạt động hóa học hoặc dãy thế điện cực chuẩn xem nào! Cặp oxi hóa - khử Cu²⁺/Cu có thế chuẩn E° = +0.34V, liệu ion H⁺ (0.00V) có đủ mạnh để oxi hóa được Cu không?";
    } else if (lower.includes('kẽm') || lower.includes('zn') || lower.includes('hcl')) {
      reply = "💡 **Gợi ý Socratic:** Khi mẩu Zn tiếp xúc với HCl, bọt khí nổi lên là khí gì? Em hãy quan sát ở góc màn hình 'Vi mô': các electron đang di chuyển từ nguyên tử nào sang ion nào?";
    } else if (lower.includes('natri') || lower.includes('na') || lower.includes('nổ') || lower.includes('cháy')) {
      reply = "⚠️ **Gợi ý Socratic:** Natri là kim loại kiềm nhóm IA có năng lượng ion hóa rất thấp. Tại sao khi thả vào nước mẩu Na lại nóng chảy thành viên tròn và bốc cháy? Phản ứng này tỏa ra bao nhiêu nhiệt lượng (ΔrH°)?";
    } else if (lower.includes('kết tủa') || lower.includes('baso4') || lower.includes('bari')) {
      reply = "💡 **Gợi ý Socratic:** Để phản ứng trao đổi ion trong dung dịch chất điện li xảy ra, cần có ít nhất một trong ba điều kiện nào? Hãy kiểm tra tính tan của muối Bari sunfat (BaSO4) trong nước và trong axit xem nhé!";
    } else if (lower.includes('nâu đỏ') || lower.includes('no2') || lower.includes('độc')) {
      reply = "⚠️ **Gợi ý Socratic:** Khí màu nâu đỏ bốc lên chính là Nitrogen dioxide (NO2). Số oxi hóa của Nitơ đã thay đổi từ bao nhiêu trong HNO3 về bao nhiêu trong NO2? Vì sao thí nghiệm này bắt buộc phải làm trong tủ hút?";
    }

    res.json({ text: reply });
  });

  // Socratic evaluation endpoint
  app.post('/api/socratic/evaluate', async (req, res) => {
    const { studentText, outcome } = req.body;
    const ai = getGeminiClient();

    if (ai && outcome) {
      try {
        const prompt = `
Bạn là Trợ lý giáo dục Hóa học THPT theo chương trình GDPT 2018.
Hãy chấm điểm và nhận xét câu giải thích của học sinh về thí nghiệm sau:
- Phản ứng: ${outcome.balancedEquation}
- Phương trình ion rút gọn: ${outcome.netIonicEquation}
- Bản chất vi mô: ${outcome.microExplanationVi}
- Năng lượng phản ứng: ΔrH° = ${outcome.deltaH} kJ/mol (${outcome.deltaH < 0 ? "Tỏa nhiệt" : "Thu nhiệt"})

Câu trả lời của học sinh: "${studentText}"

Yêu cầu:
1. Đánh giá tính chính xác về mặt hóa học (đúng/sai/thiếu sót).
2. Khen ngợi điểm học sinh hiểu đúng (sự chuyển dịch electron, cation/anion, hiện tượng vĩ mô).
3. Đặt 1 câu hỏi Socratic gợi mở để học sinh đào sâu thêm bản chất.
4. Giữ giọng điệu sư phạm, khích lệ và ngắn gọn (dưới 120 từ).
`.trim();

        const response = await ai.models.generateContent({
          model: 'gemini-2.5-flash',
          contents: prompt
        });

        if (response.text) {
          return res.json({ text: response.text });
        }
      } catch (err: any) {
        console.warn('Gemini Socratic evaluate API error, using local fallback:', err?.message || err);
      }
    }

    // Local NLP Rubric scoring
    const text = (studentText || '').toLowerCase();
    let score = 0;
    const feedbackItems: string[] = [];

    if (text.includes('electron') || text.includes('nhường') || text.includes('nhận') || text.includes('oxi hóa') || text.includes('khử')) {
      score += 35;
      feedbackItems.push('✓ Đã nhận diện đúng bản chất chuyển dịch electron / quá trình oxi hóa khử.');
    }
    if (text.includes('ion') || text.includes('h+') || text.includes('zn2+') || text.includes('kết tủa') || text.includes('liên kết')) {
      score += 35;
      feedbackItems.push('✓ Nêu được tương tác giữa các ion/hạt ở cấp độ vi mô.');
    }
    if (text.includes('tỏa nhiệt') || text.includes('thu nhiệt') || text.includes('nhiệt độ') || text.includes('enthalpy') || text.includes('năng lượng')) {
      score += 30;
      feedbackItems.push('✓ Có phân tích yếu tố nhiệt phản ứng và năng lượng liên kết theo GDPT 2018.');
    }

    const totalScore = Math.max(score, 30);
    const rankStr = totalScore >= 80 ? "Rất xuất sắc (Mức 3 - Vận dụng cao)" : totalScore >= 50 ? "Khá tốt (Mức 2 - Thông hiểu)" : "Cần bổ sung (Mức 1 - Nhận biết)";

    let feedback = `📊 **Đánh giá năng lực hóa học GDPT 2018:** ${rankStr} (${totalScore}/100 điểm)\n\n`;
    if (feedbackItems.length > 0) {
      feedback += feedbackItems.join('\n') + '\n';
    } else {
      feedback += '• Em đã ghi nhận được hiện tượng cơ bản, nhưng cần bổ sung thêm giải thích về mặt hạt ion và số electron trao đổi.\n';
    }
    if (outcome?.microExplanationVi) {
      feedback += `\n🎯 **Bản chất chuẩn:** ${outcome.microExplanationVi}`;
    }

    res.json({ text: feedback });
  });

  // Vite middleware for development
  if (process.env.NODE_ENV !== 'production') {
    const { createServer: createViteServer } = await import('vite');
    const vite = await createViteServer({
      server: { middlewareMode: true },
      appType: 'spa',
    });
    app.use(vite.middlewares);
  } else {
    const distPath = path.join(process.cwd(), 'dist');
    app.use(express.static(distPath));
    app.get('*', (req, res) => {
      res.sendFile(path.join(distPath, 'index.html'));
    });
  }

  app.listen(PORT, '0.0.0.0', () => {
    console.log(`Smart ChemLab server running on http://0.0.0.0:${PORT}`);
  });
}

startServer();
