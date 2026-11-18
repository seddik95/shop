import { TestBed } from "@angular/core/testing";
import { ContactComponent } from "./contact.component";

describe("ContactComponent", () => {
  function create() {
    TestBed.configureTestingModule({ imports: [ContactComponent] });
    const f = TestBed.createComponent(ContactComponent);
    f.detectChanges();
    return f;
  }

  it("requires email and message", () => {
    const c = create().componentInstance;
    expect(c.form.valid).toBeFalse();
  });

  it("rejects messages of 300 characters or more", () => {
    const c = create().componentInstance;
    c.form.setValue({ email: "a@b.com", message: "x".repeat(300) });
    expect(c.form.valid).toBeFalse();
    c.form.setValue({ email: "a@b.com", message: "x".repeat(299) });
    expect(c.form.valid).toBeTrue();
  });

  it("shows the success message after sending", () => {
    const f = create();
    f.componentInstance.form.setValue({ email: "a@b.com", message: "hello" });
    f.componentInstance.send();
    f.detectChanges();
    expect(f.nativeElement.textContent).toContain("Demande de contact envoyée avec succès");
  });
});
