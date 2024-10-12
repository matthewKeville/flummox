
// TODO when http is exchanged for https
// modern clipboard access is through the navigator api,
// but it requires https, this is a hack sourced from
// https://stackoverflow.com/questions/72237719/not-being-able-to-copy-url-to-clipboard-without-adding-the-protocol-https

export function CopyToClipboardInsecure(text) {
    const textArea = document.createElement("textarea");
    textArea.value = text;
    document.body.appendChild(textArea);
    textArea.focus();
    textArea.select();
    try {
          document.execCommand('copy');
        } catch (err) {
              console.error('Unable to copy to clipboard', err);
            }
    document.body.removeChild(textArea);
}
